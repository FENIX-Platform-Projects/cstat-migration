package org.fao.ess.cstat.migration.logic.business;


import org.fao.ess.cstat.migration.dto.cstat.CSColumn;
import org.fao.ess.cstat.migration.dto.cstat.CSDSD;
import org.fao.ess.cstat.migration.dto.cstat.CSDataset;
import org.fao.ess.cstat.migration.dto.cstat.CSValue;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.type.DataType;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class Translator {

    private static final HashMap<String,String> subjects = new HashMap<>();
    private String lang;

    static {
        subjects.put("GEO","geo");
        subjects.put("UM","um");
        subjects.put("UNIT","um");
        subjects.put("VALUE","value");
        subjects.put("ITEM","item");
        subjects.put("INDICATOR","indicator");
        subjects.put("FLAG","flag");
    }


    public Resource translateDAO (CSDataset dataset, String countryISO,String lang) throws Exception {

        this.lang = lang;

        Resource<DSDDataset,Object[]> resource = new Resource<>();
        try {
            resource.setMetadata(setMetadata(dataset, countryISO));
            setData(resource.getMetadata().getDsd(),dataset.getData());

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("here");


        return resource;
    }


    private MeIdentification<DSDDataset> setMetadata (CSDataset dataset, String countryISO) throws Exception {

        MeIdentification<DSDDataset> metadata = new MeIdentification<>();

        if(dataset.getUid()== null)
            System.out.println("ERROR");

        // uid, title, creation date, update date
        metadata.setUid(dataset.getUid());
        metadata.setTitle(dataset.getTitle());

        metadata.setCreationDate(dataset.getCreationDate());
        metadata.setLastUpdate(dataset.getUpdateDate());

        metadata.setDsd(setDSD(dataset.getDsd(),countryISO));


        return metadata;





    }



    private DSDDataset setDSD (CSDSD oldDSD,String countryISO) throws Exception {

        // context system
        DSDDataset dsdDataset = new DSDDataset();
        dsdDataset.setContextSystem("cstat_"+countryISO);

        Collection<DSDColumn> columns = new LinkedList<>();

        // columns
        for(CSColumn csColumn: oldDSD.getColumns()){
            String columnSubject = csColumn.getDimension()!= null? csColumn.getDimension().getName(): null;
            // if the subject is ok!
            if(columnSubject!= null && subjects.keySet().contains(columnSubject)){
                DSDColumn column = new DSDColumn();

                // set id, title, datatype
                column.setId(csColumn.getColumnId());
                column.setTitle(csColumn.getTitle());
                column.setDataType(DataType.valueOf(csColumn.getDataType()));
                column.setSubject(subjects.get(csColumn.getDimension().getName()));


                // different between different datatypes
                if (column.getDataType() == DataType.code ||  column.getDataType() == DataType.customCode) {
                        DSDDomain domain = new DSDDomain();
                        OjCodeList codeList = new OjCodeList();

                        if(column.getDataType() == DataType.code){
                            // id, title, description, version of the codelist
                            codeList.setIdCodeList(csColumn.getCodeSystem().getSystem());
                            codeList.setVersion(csColumn.getCodeSystem().getVersion());
                            codeList.setExtendedName(csColumn.getTitle());


                        }else if(column.getDataType() == DataType.customCode) {
                            if (csColumn.getValuesCountrystat() == null)
                                throw new Exception("there should be codes into the dsd for the column " + csColumn.getColumnId());

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
                }


                columns.add(column);
            }
            dsdDataset.setColumns(columns);

        }

        return dsdDataset;

    }


    private Collection<Object[]> setData (DSDDataset dataset, Collection<Object> csData ) {

        Collection<Object[]> data = new LinkedList<>();

        System.out.println(csData);


        return data;
    }




}
