package org.fao.ess.cstat.migration;


import org.fao.ess.cstat.migration.logic.Logic;
import org.fao.ess.cstat.migration.logic.LogicFactory;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Manager {
    private static final Logger log = Logger.getGlobal();

    public static void main(String[] args) {
        Logic logic = LogicFactory.getInstance(args.length>0 ? args[0] : null);
        if (logic!=null)
            try {
                logic.execute(Arrays.copyOfRange(args, 1, args.length));
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        else
            log.log(Level.SEVERE, "Migration command not found");
    }
}
