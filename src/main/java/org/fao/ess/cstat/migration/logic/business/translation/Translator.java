package org.fao.ess.cstat.migration.logic.business.translation;


import org.fao.ess.cstat.migration.dto.cstat.CSColumn;
import org.fao.ess.cstat.migration.dto.cstat.CSDSD;
import org.fao.ess.cstat.migration.dto.cstat.CSDataset;
import org.fao.ess.cstat.migration.dto.cstat.CSValue;
import org.fao.ess.cstat.migration.dto.subjects.SubjectTitle;
import org.fao.ess.cstat.migration.logic.business.transcode.CodelistD3S;
import org.fao.fenix.commons.mdsd.annotations.Subject;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.type.DataType;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;

import java.util.*;

public class Translator {

    private static final HashMap<String, String> subjects = new HashMap<>();
    private static final Set<String> keySubjects = new HashSet<>();

    private HashMap<String, Object> virtualColumnsValues;
    private HashMap<String, String> codelistToColumnID;
    private Set<String> keyColumns;
    private List<String> columnsID, datatypes;
    private String lang;
    private boolean errorsOnDSD ;
    private SubjectTitle subjectTitles;

    static {
        subjects.put("GEO", "geo");
        subjects.put("UM", "um");
        subjects.put("UNIT", "um");
        subjects.put("VALUE", "value");
        subjects.put("ITEM", "item");
        subjects.put("INDICATOR", "indicator");
        subjects.put("FLAG", "flag");
        subjects.put("TIME", "time");

        keySubjects.addAll(new LinkedList<>(Arrays.asList("geo", "time", "indicator", "item")));
    }


    // init
    public Resource translateDAO(CSDataset dataset, String countryISO, String lang, Map<String, List<String>> errors, SubjectTitle subjectTitles) throws Exception {
        this.subjectTitles = subjectTitles;
        errorsOnDSD= false;
        keyColumns = new HashSet<>();
        this.lang = lang;
        Resource<DSDDataset, Object[]> resource = new Resource<>();
        try {
            resource.setMetadata(setMetadata(dataset, countryISO, errors));
            resource.setData(setData(resource.getMetadata().getDsd(), dataset.getData(), errors, resource.getMetadata().getUid()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        errorsOnDSD = errorsOnDSD;
        return resource;
    }


    // business
    private MeIdentification<DSDDataset> setMetadata(CSDataset dataset, String countryISO, Map<String, List<String>> errors) throws Exception {


        MeIdentification<DSDDataset> metadata = new MeIdentification<>();

        if (dataset.getUid() == null)
            throw new Exception("ERROR DSD: this dataset does not contain an uid");

        // uid, title, creation date, update date
        metadata.setUid(dataset.getUid());
        metadata.setTitle(dataset.getTitle());
        // creation date, update date, representationType and dsd
        metadata.setCreationDate(dataset.getCreationDate());
        MeContent meContent = new MeContent();
        meContent.setResourceRepresentationType(RepresentationType.dataset);
        metadata.setMeContent(meContent);
        metadata.setLastUpdate(dataset.getUpdateDate());
        metadata.setDsd(setDSD(dataset.getDsd(), countryISO, errors, metadata.getUid()));
        return metadata;
    }


    private DSDDataset setDSD(CSDSD oldDSD, String countryISO,Map<String, List<String>> errors, String uid) throws Exception {

        Map<String, String> subjectsToID = new HashMap<>();

        virtualColumnsValues = new HashMap<>();
        codelistToColumnID = new HashMap<>();
        columnsID = new ArrayList<>();
        datatypes = new ArrayList<>();

        // context system and datasources
        DSDDataset dsdDataset = new DSDDataset();
        dsdDataset.setContextSystem("cstat_" + countryISO.toLowerCase());
        dsdDataset.setDatasources(new String[]{"D3S"});

        Collection<DSDColumn> columns = new LinkedList<>();

        // columns
        for (CSColumn csColumn : oldDSD.getColumns()) {
            String columnSubject = csColumn.getDimension() != null ? csColumn.getDimension().getName() : null;

            //if the subject is other
            if (columnSubject.equals("OTHER")) {
                errorsOnDSD = true;
                handleErrors(errors, uid, "DSD ERROR: the dataset contains the subject equals to OTHER in the column " + csColumn.getColumnId());
/*
                throw new Exception("OTHER column present in dataset: "+uid+  " and the column is "+ csColumn.getColumnId());
*/
            }
            //if the subject is NULL
            if (columnSubject == null) {
                errorsOnDSD  =true;
                handleErrors(errors, uid, "DSD ERROR: the dataset contains the subject equals to NULL in the column " + csColumn.getColumnId());
            }

            // if the subject is ok!
            if (columnSubject != null && subjects.keySet().contains(columnSubject)) {
                DSDColumn column = new DSDColumn();

                // ERROR if there are SUBJECT not unique
                if(subjectsToID.containsKey(columnSubject)) {
                    errorsOnDSD  =true;
                    handleErrors(errors,uid,"The subject "+ columnSubject+ " is replicated into these columns: "+subjectsToID.get(columnSubject)+" AND "+ csColumn.getColumnId());

                }

                // set id, title, datatype
                column.setId(csColumn.getColumnId());
                Map<String, String> titles= handleTitles(csColumn, uid);
                column.setTitle(titles);
                column.setDataType(DataType.valueOf(csColumn.getDataType()));
                column.setSubject(subjects.get(csColumn.getDimension().getName()));

                //add eventually KEY COLUMNS
                if (keySubjects.contains(column.getSubject())) {
                    column.setKey(true);
                    keyColumns.add(column.getId());
                }

                // utils variables
                columnsID.add(column.getId());
                datatypes.add(csColumn.getDataType());

                // ERROR with customCode
                if (column.getDataType() == DataType.customCode) {
                    errorsOnDSD = true;
                    handleErrors(errors, uid, "DSD ERROR: the dataset contains the datatype equals to customCode in the column " + csColumn.getColumnId());
                    //TODO there is a custom code into one dataset
//                    throw new Exception("CUSTOM CODE");
                }
                // different between different datatypes
                if (column.getDataType() == DataType.code) {
                    OjCodeList codeList = new OjCodeList();

                    //if it is a virtual column
                    if (csColumn.getVirtualColumn() != null) {
                        checkVirtualColumnCondition(csColumn, column.getId());
                        LinkedHashMap<String, String> codedValues = (LinkedHashMap<String, String>) ((List) csColumn.getValues()).get(0);
                        if (codedValues.get("code") == null)
                            handleErrors(errors, uid,"DSD ERROR: this column" + column.getId() + "should not have the field code empty into the values section");
                        virtualColumnsValues.put(column.getId(), codedValues.get("code"));
                    }

                    //if it is a code column
                    if (column.getDataType() == DataType.code) {

                        // check the codelist has the id
                        if (csColumn.getCodeSystem().getSystem() == null)
                            handleErrors(errors, uid,"DSD ERROR: codelist on columns:" + column.getId() + " should have an id to be identified");

                        //set id, title, description, version of the codelist
                        codeList.setIdCodeList(CodelistD3S.valueOf(csColumn.getCodeSystem().getSystem()).getCodelistID());
                        codeList.setVersion(CodelistD3S.valueOf(csColumn.getCodeSystem().getSystem()).getVersion());
                        codeList.setExtendedName(csColumn.getTitle());
                        codelistToColumnID.put(csColumn.getCodeSystem().getSystem(), column.getId());
                    }
                    //TODO ASAP for handling custom code
                  /*  else if (column.getDataType() == DataType.customCode) {
                        if (csColumn.getValuesCountrystat() == null)
                            handleErrors(errors, uid,"there should be codes into the dsd for the column " + csColumn.getColumnId());

                        // put codes into the new customCode column of the D3S
                        Collection<OjCode> codes = new LinkedList<>();
                        for (CSValue cstatCode : csColumn.getValuesCountrystat()) {
                            OjCode code = new OjCode();
                            code.setCode(cstatCode.getCode());
                            HashMap<String, String> labelI18n = new HashMap<>();
                            labelI18n.put(lang, cstatCode.getLabel());
                            code.setLabel(labelI18n);
                        }
                        codeList.setCodes(codes);
                    }*/
                } else {
                    // virtual column for datatypes different from coded ones
                    if (csColumn.getVirtualColumn() != null) {
                        checkVirtualColumnCondition(csColumn, column.getId());
                        virtualColumnsValues.put(column.getId(), ((List) csColumn.getValues()).get(0));
                    }
                }
                columns.add(column);
            }
            dsdDataset.setColumns(columns);
        }

        return dsdDataset;

    }




    private Collection<Object[]> setData(DSDDataset dataset, Collection<Object> csData, Map<String, List<String>> errors, String uid) throws Exception {

        Collection<Object[]> data = new LinkedList<>();

        List<Object> originalData = (ArrayList) csData;

        // for each row
        for (int i = 0, size = originalData.size(); i < size; i++) {
            Object[] newRow = new Object[dataset.getColumns().size()];
            LinkedHashMap<String, String> originalRow = (LinkedHashMap<String, String>) originalData.get(i);
            LinkedList<DSDColumn> rowsDSD = (LinkedList<DSDColumn>) dataset.getColumns();
            // for each column in the new dsd
            for (int j = 0, columnsSize = dataset.getColumns().size(); j < columnsSize; j++) {
                Object val = virtualColumnsValues.get(rowsDSD.get(j).getId()) != null ? virtualColumnsValues.get(rowsDSD.get(j).getId()) : originalRow.get(rowsDSD.get(j).getId());

                // parse data with subject equals to time to INTEGER
                if (rowsDSD.get(j).getSubject() == "time")
                    val = Integer.parseInt(val.toString());

                // PROBLEM WITH ARRAY
                if (val instanceof List) {
                    List<Object> arrayVal = (List<Object>) val;
                    if (arrayVal.size() > 1) {
                        handleErrors(errors, uid,"The size of the value for the column " + rowsDSD.get(j).getId() + "  at row: " + i + " , is greater than 1");
                        throw new Exception("The size of the value for the column " + rowsDSD.get(j).getId() + "  at row: " + i + " , is greater than 1");
                    }
                    val = arrayVal.get(0);
                }
                newRow[j] = val;
            }
            data.add(newRow);
        }
        return data;
    }


    // utils
    private void checkVirtualColumnCondition(CSColumn csColumn, String columnId) throws Exception {
        if (csColumn.getValues() == null)
            throw new Exception("this column " + columnId + " cannot have the field values empty");
        if (csColumn.getValues().size() == 0 || csColumn.getValues().size() > 1)
            throw new Exception("this column " + columnId + " must have the size of values field  == 1");
    }

    public List<String> getColumnsID() {
        return columnsID;
    }

    public List<String> getDatatypes() {
        return datatypes;
    }

    public HashMap<String, String> getCodelistToColumnID() {
        return codelistToColumnID;
    }

    public Set<String> getKeyColumns() {
        return keyColumns;
    }

    private void handleErrors (Map<String, List<String>> errors, String uid, String messageError) {
        List<String> values = new LinkedList<>();
        if(errors.containsKey(uid))
            values = errors.get(uid);
        values.add(messageError);
        errors.put(uid,values);
    }

    private Map<String,String> handleTitles(CSColumn csColumn, String uid) {

        Map<String,String> titles = null;

        if(csColumn.getTitle() != null) {
            titles = csColumn.getTitle();
        }
        else if(csColumn.getDimension().getTitle() != null) {
            titles = csColumn.getDimension().getTitle();
        }
        else {
            switch (csColumn.getDimension().getName()){
                case "GEO":
                   titles = subjectTitles.getGeo().get(uid.substring(3,4));
                    break;

                case "UM":
                case "UNIT":
                    titles = subjectTitles.getUm();
                    break;

                case "VALUE":
                    titles = subjectTitles.getValue();
                    break;

                case "ITEM":
                    titles = subjectTitles.getItem();

                    break;
                case "INDICATOR":
                    titles = subjectTitles.getIndicator();

                    break;

                case "FLAG":
                    titles = subjectTitles.getFlag();
                    break;

                case "TIME":
                    titles = subjectTitles.getItem();
                    break;
            }
        }
        return titles;
    }


    public boolean isErrorsOnDSD() {
        return errorsOnDSD;
    }
}
