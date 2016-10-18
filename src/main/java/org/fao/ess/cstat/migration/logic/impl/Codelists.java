package org.fao.ess.cstat.migration.logic.impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fao.ess.cstat.migration.logic.Command;
import org.fao.ess.cstat.migration.logic.Logic;
import org.fao.ess.cstat.migration.utils.xml.XMLParser;

@Command("codelist")
public class Codelists implements Logic {
    private static final Logger LOGGER = LogManager.getLogger("Codelists");

    @Override
    public void execute(String... args) throws Exception {

        LOGGER.debug("debug");
        LOGGER.info("info");

        LOGGER.log(Level.getLevel("verbose"), "a diagnostic message");

        XMLParser.getAllDatasetFromSchema("BFA");



    }
}
