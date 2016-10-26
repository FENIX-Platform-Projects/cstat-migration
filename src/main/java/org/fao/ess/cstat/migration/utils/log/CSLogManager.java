package org.fao.ess.cstat.migration.utils.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class CSLogManager {

    private static HashMap<Integer,String> externalLevels = new HashMap<>();
    private static HashMap<Integer,String> internalLevels = new HashMap<>();

    static {
        externalLevels.put(1,"X");
        externalLevels.put(2,"Y");
        externalLevels.put(3,"Z");

        internalLevels.put(1,"NOTICE");
        internalLevels.put(2,"WARN");
        internalLevels.put(3,"ERROR");

    }

    public  void writeExternalMessage(Logger logger, int level, String message) {
        logger.log(Level.getLevel(externalLevels.get(level)),message);
    }

    public  void writeInternalMessage(Logger logger, int level, String message) {
        logger.log(Level.getLevel(internalLevels.get(level)),message);
    }

    public  void writeBothMessage(Logger logger, int level, String message) {
        logger.log(Level.getLevel(internalLevels.get(level)),message);
        logger.log(Level.getLevel(externalLevels.get(level)),message);
    }
}
