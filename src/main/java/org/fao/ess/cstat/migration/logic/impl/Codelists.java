package org.fao.ess.cstat.migration.logic.impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fao.ess.cstat.migration.dao.InputDao;
import org.fao.ess.cstat.migration.db.connection.DBAdapter;
import org.fao.ess.cstat.migration.logic.Command;
import org.fao.ess.cstat.migration.logic.Logic;
import org.fao.ess.cstat.migration.logic.business.Translator;
import org.fao.ess.cstat.migration.utils.xml.XMLParser;

import javax.inject.Inject;

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


        translator.translateDAO(input.loadDataset("233CPD010"), "BFA", "EN");

        String[] columns= new String[]{"one","two","three"};
        String[] types= new String[]{"customCode","year","number"};
        System.out.println(dbAdapter.createTable("test",columns,types ));


    }
}
