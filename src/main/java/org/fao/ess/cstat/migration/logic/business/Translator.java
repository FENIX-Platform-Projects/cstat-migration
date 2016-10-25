package org.fao.ess.cstat.migration.logic.business;


import org.fao.ess.cstat.migration.dto.cstat.CSColumn;
import org.fao.ess.cstat.migration.dto.cstat.CSDSD;
import org.fao.ess.cstat.migration.dto.cstat.CSDataset;
import org.fao.ess.cstat.migration.dto.cstat.CSValue;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.type.DataType;
import java.util.*;

public class Translator {

    private static final HashMap<String, String> subjects = new HashMap<>();
    private HashMap<String, Object> virtualColumnsValues;
    private HashMap<String, String> codelistMap;

    private List<String> columnsID;
    private List<String> datatypes;
    private String lang;

    static {
        subjects.put("GEO", "geo");
        subjects.put("UM", "um");
        subjects.put("UNIT", "um");
        subjects.put("VALUE", "value");
        subjects.put("ITEM", "item");
        subjects.put("INDICATOR", "indicator");
        subjects.put("FLAG", "flag");
    }


    // init
    public Resource translateDAO(CSDataset dataset, String countryISO, String lang) throws Exception {
        this.lang = lang;
        Resource<DSDDataset, Object[]> resource = new Resource<>();
        try {
            resource.setMetadata(setMetadata(dataset, countryISO));
            resource.setData(setData(resource.getMetadata().getDsd(), dataset.getData()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resource;
    }


    // business
    private MeIdentification<DSDDataset> setMetadata(CSDataset dataset, String countryISO) throws Exception {

        MeIdentification<DSDDataset> metadata = new MeIdentification<>();

        if (dataset.getUid() == null)
            throw new Exception("ERROR DSD: this dataset does not contain an uid");

        // uid, title, creation date, update date
        metadata.setUid(dataset.getUid());
        metadata.setTitle(dataset.getTitle());
        // creation date, update date and dsd
        metadata.setCreationDate(dataset.getCreationDate());
        metadata.setLastUpdate(dataset.getUpdateDate());
        metadata.setDsd(setDSD(dataset.getDsd(), countryISO));
        return metadata;
    }


    private DSDDataset setDSD(CSDSD oldDSD, String countryISO) throws Exception {

        virtualColumnsValues = new HashMap<>();
        codelistMap = new HashMap<>();
        columnsID = new ArrayList<>();
        datatypes = new ArrayList<>();

        // context system
        DSDDataset dsdDataset = new DSDDataset();
        dsdDataset.setContextSystem("cstat_" + countryISO);

        Collection<DSDColumn> columns = new LinkedList<>();

        // columns
        for (CSColumn csColumn : oldDSD.getColumns()) {
            String columnSubject = csColumn.getDimension() != null ? csColumn.getDimension().getName() : null;

            //if the subject is other
            if (columnSubject == "OTHER")
                throw new Exception("DSD ERROR: this dataset contains in the column " + csColumn.getColumnId() + " the subject equals to OTHER");

            // if the subject is ok!
            if (columnSubject != null && subjects.keySet().contains(columnSubject)) {
                DSDColumn column = new DSDColumn();

                // set id, title, datatype
                column.setId(csColumn.getColumnId());
                column.setTitle(csColumn.getTitle());
                column.setDataType(DataType.valueOf(csColumn.getDataType()));
                column.setSubject(subjects.get(csColumn.getDimension().getName()));

                // utils variables
                columnsID.add(column.getId());
                datatypes.add(csColumn.getDataType());

                // different between different datatypes
                if (column.getDataType() == DataType.code || column.getDataType() == DataType.customCode) {
                    OjCodeList codeList = new OjCodeList();

                    //if it is a virtual column
                    if (csColumn.getVirtualColumn() != null) {
                        checkVirtualColumnCondition(csColumn, column.getId());
                        LinkedHashMap<String, String> codedValues = (LinkedHashMap<String, String>) ((List) csColumn.getValues()).get(0);
                        if (codedValues.get("code") == null)
                            throw new Exception("DSD ERROR: this column" + column.getId() + "should not have the field code empty into the values section");
                        virtualColumnsValues.put(column.getId(), codedValues.get("code"));
                    }

                    //if it is a code column
                    if (column.getDataType() == DataType.code) {

                        // check the codelist has the id
                        if (csColumn.getCodeSystem().getSystem() == null)
                            throw new Exception("DSD ERROR: codelist on columns:" + column.getId() + " should have an id to be identified");

                        //set id, title, description, version of the codelist
                        codeList.setIdCodeList(csColumn.getCodeSystem().getSystem());
                        codeList.setVersion(csColumn.getCodeSystem().getVersion());
                        codeList.setExtendedName(csColumn.getTitle());
                        codelistMap.put(csColumn.getCodeSystem().getSystem(), column.getId());
                    }
                    //if it is a customCode column
                    else if (column.getDataType() == DataType.customCode) {
                        if (csColumn.getValuesCountrystat() == null)
                            throw new Exception("there should be codes into the dsd for the column " + csColumn.getColumnId());

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
                    }
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


    private Collection<Object[]> setData(DSDDataset dataset, Collection<Object> csData) throws Exception {

        Collection<Object[]> data = new LinkedList<>();

        System.out.println(csData);
        List<Object> originalData = (ArrayList) csData;

        // for each row
        for (int i = 0, size = originalData.size(); i < size; i++) {
            Object[] newRow = new Object[dataset.getColumns().size()];
            LinkedHashMap<String, String> originalRow = (LinkedHashMap<String, String>) originalData.get(i);
            LinkedList<DSDColumn> rowsDSD = (LinkedList<DSDColumn>) dataset.getColumns();
            // for each column in the new dsd
            for (int j = 0, columnsSize = dataset.getColumns().size(); j < columnsSize; j++) {
                Object val = virtualColumnsValues.get(rowsDSD.get(j).getId()) != null ? virtualColumnsValues.get(rowsDSD.get(j).getId()) : originalRow.get(rowsDSD.get(j).getId());
                if (val instanceof List) {
                    List<Object> arrayVal = (List<Object>) val;
                    if (arrayVal.size() > 1)
                        throw new Exception("The size of the value for the column " + rowsDSD.get(j).getId() + " is greater than 1");
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

    public HashMap<String, String> getCodelistMap() {
        return codelistMap;
    }
}
