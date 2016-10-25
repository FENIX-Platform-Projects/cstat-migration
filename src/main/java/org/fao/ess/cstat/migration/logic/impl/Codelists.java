package org.fao.ess.cstat.migration.logic.impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fao.ess.cstat.migration.dao.InputDao;
import org.fao.ess.cstat.migration.db.connection.DBAdapter;
import org.fao.ess.cstat.migration.logic.Command;
import org.fao.ess.cstat.migration.logic.Logic;
import org.fao.ess.cstat.migration.logic.business.Transcode;
import org.fao.ess.cstat.migration.logic.business.Translator;
import org.fao.ess.cstat.migration.utils.xml.XMLParser;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

@Command("codelist")
public class Codelists implements Logic {
    private static final Logger LOGGER = LogManager.getLogger("Codelists");
    private @Inject InputDao input;
    private @Inject DBAdapter dbAdapter;
    private @Inject Translator translator;



    @Override
    public void execute(String... args) throws Exception {

        LOGGER.debug("debug");
        LOGGER.info("info");

        LOGGER.log(Level.getLevel("verbose"), "a diagnostic message");

        XMLParser.getAllDatasetFromSchema("BFA");

        //Resource<DSDCodelist, Code> codelistCodeResource = input.loadCodelist("CS_FoodSupply","1.0");

        Resource<DSDDataset,Object[]> resource = translator.translateDAO(input.loadDataset("233CPD010"), "BFA", "EN");

        dbAdapter.createTable(resource.getMetadata().getUid(),translator.getColumnsID(),translator.getDatatypes() );

        dbAdapter.insertValues(resource.getMetadata().getUid(),resource.getData());

        Map<String,String> map  = translator.getCodelistMap();

        for(String key: map.keySet()) {
            System.out.println("transcoding this codelist: "+key+ " into the dataset "+resource.getMetadata().getUid());
            if(Transcode.valueOf(key).getDataset()!= null)
                 dbAdapter.transcodeData(resource.getMetadata().getUid(), map.get(key), Transcode.valueOf(key).getDataset());
        }

        resource.setData(dbAdapter.getData(resource.getMetadata().getUid()));

        System.out.println("here");





    }
}
