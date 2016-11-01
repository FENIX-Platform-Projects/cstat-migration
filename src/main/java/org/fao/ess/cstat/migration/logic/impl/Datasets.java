package org.fao.ess.cstat.migration.logic.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fao.ess.cstat.migration.dao.InputDao;
import org.fao.ess.cstat.migration.dao.OutputDao;
import org.fao.ess.cstat.migration.db.connection.DBAdapter;
import org.fao.ess.cstat.migration.dto.config.dataset.CSConfig;
import org.fao.ess.cstat.migration.dto.config.dataset.CSFilter;
import org.fao.ess.cstat.migration.dto.cstat.CSDataset;
import org.fao.ess.cstat.migration.dto.subjects.SubjectTitle;
import org.fao.ess.cstat.migration.logic.Command;
import org.fao.ess.cstat.migration.logic.Logic;
import org.fao.ess.cstat.migration.logic.business.transcode.CodelistDB;
import org.fao.ess.cstat.migration.logic.business.translation.Translator;
import org.fao.ess.cstat.migration.logic.business.validation.Validator;
import org.fao.ess.cstat.migration.utils.json.JSONParser;
import org.fao.ess.cstat.migration.utils.log.CSLogManager;
import org.fao.ess.cstat.migration.utils.xml.XMLParser;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;

import javax.inject.Inject;
import java.io.File;
import java.util.*;

@Command("dataset")
public class Datasets implements Logic {
    private static final Logger LOGGER = LogManager.getLogger("Controller");
    private @Inject CSLogManager csLogManager;

    private @Inject InputDao input;
    private @Inject OutputDao outputDao;
    private @Inject DBAdapter dbAdapter;
    private @Inject Translator translator;
    private @Inject Validator validator;
    private static final String URL_CONFIG = "/home/faber-cst/Projects/cstat-migration/config/start/config.json";
    private static final String URL_SUBJECTS = "/home/faber-cst/Projects/cstat-migration/config/subjectTitles/titles.json";

    private CSConfig csConfig;
    private SubjectTitle subjectTitles;
    private Map<String, List<String>> datasets;
    private Map<String, List<String>> errors;
    private HashSet<String> goodUids;


    @Override
    public void execute(String... args) throws Exception {

        List<Resource> resources = new LinkedList<>();

        goodUids = new HashSet<>();


        System.out.println("--------------STARTED---------------"+"\n");
        errors = new HashMap<>();
        csLogManager.writeInternalMessage(LOGGER, 1, "********** START ***********");

        csConfig = JSONParser.toObject(getConfigFile(), CSConfig.class);
        subjectTitles = JSONParser.toObject(getUrlSubjects(), SubjectTitle.class);

        if (csConfig == null || csConfig.getCountry() == null)
            handleException("CONFIGURATION ERROR : config file does not have a country");

        csLogManager.writeInternalMessage(LOGGER, 1, "Starting processing datasets for the country: " + csConfig.getCountry());


        // check if there are some filters with allowed or not allowed datasets
        datasets = (areThereAnyFilter(csConfig.getFilters())) ?
                XMLParser.trasformFlatData(XMLParser.getAllDatasetFromSchema(csConfig.getCountry()),
                        csConfig.getFilters().getDataset().getExcludedID(),
                        csConfig.getFilters().getDataset().getIncludedID()) :
                XMLParser.trasformFlatData(XMLParser.getAllDatasetFromSchema(csConfig.getCountry()));

       /* // PROBLEM with XML
        if (datasets.keySet().size() != 2)
            throw new Exception("XML PARSER PROBLEM: there should be at least two domain in the dataset taken");
*/
        // for each domain
        for (String key : datasets.keySet()) {

            // PROBLEM with XML
            if (datasets.get(key).size() == 0)
                throw new Exception("XML PARSER PROBLEM: there should be at least one dataset for the domain " + key);

            List<String> uids = datasets.get(key);
            // for each uid
            for (int i = 0, size = uids.size(); i < size; i++) {
                csLogManager.writeInternalMessage(LOGGER, 1, "------------------- START DATASET: " + uids.get(i) + "--------");


                //translate input dao
                CSDataset inputDataset = input.loadDataset(uids.get(i), errors);

                // if the dataset has veen loaded correctly
                if (inputDataset != null && inputDataset.getUid()!= null && inputDataset.getData()!= null && inputDataset.getData().size()>0) {
                    Resource<DSDDataset, Object[]> resource = translator.translateDAO(inputDataset, csConfig.getCountry(), csConfig.getLanguage(), errors, subjectTitles);

                    System.out.println("Processing dataset: "+resource.getMetadata().getUid());

                    // create table
                    dbAdapter.createTable(resource.getMetadata().getUid(), translator.getColumnsID(), translator.getDatatypes());

                    // insert data
                    dbAdapter.insertValues(resource.getMetadata().getUid(), resource.getData(), translator.getDatatypes());

                    Map<String, String> codelistToColumnID = translator.getCodelistToColumnID();

                    int sizeBeforeColumnCheck = errors.containsKey(resource.getMetadata().getUid()) ? errors.get(resource.getMetadata().getUid()).size() : 0;

                    // column transcode check
                    for (String codelist : codelistToColumnID.keySet()) {
                        if (CodelistDB.contains(codelist) && CodelistDB.valueOf(codelist).getDataset() != null) {
                            Collection<Object[]> notMatchingCodes = dbAdapter.getNotMatchingCodes(resource.getMetadata().getUid(), codelistToColumnID.get(codelist), CodelistDB.valueOf(codelist).getDataset());
                            validator.checkCodelistCodes(notMatchingCodes, errors, resource.getMetadata().getUid(), codelist,codelistToColumnID.get(codelist));
                        }
                    }

                    int newSize = errors.containsKey(resource.getMetadata().getUid()) ? errors.get(resource.getMetadata().getUid()).size() : 0;

                    if ( /*newSize == sizeBeforeColumnCheck*/ 1 == 1) {
                        // // TODO: 26/10/16: when you will have the codelist, you will change this condition 1==1

                            // transcode data
                            for (String codelist : codelistToColumnID.keySet()) {
                                csLogManager.writeInternalMessage(LOGGER, 1, "TRANSCODING this codelist: " + codelist + " into the dataset " + resource.getMetadata().getUid());

                                if (CodelistDB.contains(codelist) && CodelistDB.valueOf(codelist).getDataset() != null)
                                    dbAdapter.transcodeData(resource.getMetadata().getUid(), codelistToColumnID.get(codelist), CodelistDB.valueOf(codelist).getDataset());
                            }

                        // if there are NOT errors on DSD
                             if(!translator.isErrorsOnDSD()) {
                                // integrity check
                                Collection<Object[]> notMatchingCodes = dbAdapter.getNotConstaintRows(resource.getMetadata().getUid(), translator.getKeyColumns());
                                validator.checkIntegrityCostraints(notMatchingCodes, errors, resource.getMetadata().getUid(), translator.getKeyColumns());

                        }
                        // set data in the output dao
                        resource.setData(dbAdapter.getData(resource.getMetadata().getUid()));
                        System.out.println("End dataset: "+resource.getMetadata().getUid());


                    }
                    // delete the table
                    dbAdapter.deleteTable(resource.getMetadata().getUid());
                    resources.add(resource);
                    System.out.println("Delete table : "+resource.getMetadata().getUid());

                    csLogManager.writeInternalMessage(LOGGER, 1, "------------------- END DATASET: " + uids.get(i) + "--------");
                }
            }
        }

        Set<String> toBeSaved = new HashSet<>();
        for(Resource dataset: resources ){
            if(!errors.containsKey(dataset.getMetadata().getUid())) {
                if(outputDao.storeDataset(dataset, csConfig.getLogics().isOverrideDS(), errors))
                    toBeSaved.add(dataset.getMetadata().getUid());
            }
        }

        for(String s: toBeSaved) {
            if(!errors.containsKey(s))
                goodUids.add(s);
        }


        printFinalReport();
        System.out.println("--------------FINISH---------------"+"\n");

    }


    // Utils
    private File getConfigFile() {
        return new File(URL_CONFIG);
    }

    public File getUrlSubjects() {
        return new File(URL_SUBJECTS);
    }


    private boolean areThereAnyFilter(CSFilter filter) {

        return filter.getDataset() != null &&
                ((filter.getDataset().getExcludedID() != null && filter.getDataset().getExcludedID().size() > 0)
                        || (filter.getDataset().getIncludedID() != null && filter.getDataset().getIncludedID().size() > 0)
                );

    }

    private void handleException(String error) throws Exception {
        csLogManager.writeInternalMessage(LOGGER, 3, error);
        throw new Exception(error);

    }


    private void printFinalReport () {

        int totalSize = goodUids.size()+errors.keySet().size();

        csLogManager.writeBothMessage(LOGGER,3, "************ REPORT RESULTS *****************");
        csLogManager.writeBothMessage(LOGGER, 3, "\n");
        csLogManager.writeBothMessage(LOGGER, 3, "\n");

        if(totalSize >0) {

            csLogManager.writeBothMessage(LOGGER, 3, "+++++" + "\n");
            csLogManager.writeBothMessage(LOGGER, 3, "\n");

            csLogManager.writeBothMessage(LOGGER, 3, "The total number of datasets processed was :" + totalSize + "\n");
            csLogManager.writeBothMessage(LOGGER, 3, "\n");

            csLogManager.writeBothMessage(LOGGER, 3, "+++++" + "\n");
            csLogManager.writeBothMessage(LOGGER, 3, "\n");
            csLogManager.writeBothMessage(LOGGER, 3, "\n");

            csLogManager.writeBothMessage(LOGGER, 3, "The number of datasets successful saved is: :" + goodUids.size());
            csLogManager.writeBothMessage(LOGGER, 3, "\n");
            csLogManager.writeBothMessage(LOGGER, 3, "\n");


            if (goodUids.size() > 0) {
                csLogManager.writeBothMessage(LOGGER, 3, "The successful saved datasets are: :");
                Iterator<String> uids = goodUids.iterator();
                StringBuilder stringBuilder = new StringBuilder();
                while (uids.hasNext())
                    stringBuilder.append(uids.next() + ", ");
                if (stringBuilder.length() > 2)
                    stringBuilder.setLength(stringBuilder.length() - 2);
                csLogManager.writeBothMessage(LOGGER, 3, stringBuilder.toString() + "\n");
            }

            csLogManager.writeBothMessage(LOGGER, 3, "+++++" + "\n");

            csLogManager.writeBothMessage(LOGGER, 3, "\n");
            csLogManager.writeBothMessage(LOGGER, 3, "\n");


            csLogManager.writeBothMessage(LOGGER, 3, "These are the dataset that have not been saved into the D3S");
            csLogManager.writeBothMessage(LOGGER, 3, "\n");


            for (String uid : errors.keySet()) {
                if (errors.get(uid).size() > 0) {
                    csLogManager.writeBothMessage(LOGGER, 3, "==========================================================================");
                    csLogManager.writeBothMessage(LOGGER, 3, "\n");
                    csLogManager.writeBothMessage(LOGGER, 3, "DATASET : " + uid + "  \n");

                    for (int i = 0, size = errors.get(uid).size(); i < size; i++) {
                        csLogManager.writeBothMessage(LOGGER, 3, "\n");
                        csLogManager.writeBothMessage(LOGGER, 3, i + 1 + ")" + "\n");
                        csLogManager.writeBothMessage(LOGGER, 3, errors.get(uid).get(i) + "\n");
                        csLogManager.writeBothMessage(LOGGER, 3, "--------");
                    }
                    csLogManager.writeBothMessage(LOGGER, 3, "\n");
                    csLogManager.writeBothMessage(LOGGER, 3, "==========================================================================");
                    csLogManager.writeBothMessage(LOGGER, 3, "\n");
                    csLogManager.writeBothMessage(LOGGER, 3, "\n");
                    csLogManager.writeBothMessage(LOGGER, 3, "\n");
                    csLogManager.writeBothMessage(LOGGER, 3, "\n");
                }
            }
        }else{
            csLogManager.writeBothMessage(LOGGER, 3, "\n");

            csLogManager.writeBothMessage(LOGGER, 3, "There isn't any dataset to process for this country");
            csLogManager.writeBothMessage(LOGGER, 3, "\n");


        }
        csLogManager.writeBothMessage(LOGGER,3, "********************  END   ********************");


    }
}
