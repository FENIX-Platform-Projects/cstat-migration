package org.fao.ess.cstat.migration.logic.impl;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fao.ess.cstat.migration.dao.InputDao;
import org.fao.ess.cstat.migration.dao.OutputDao;
import org.fao.ess.cstat.migration.dto.codelist.CSCodelist;
import org.fao.ess.cstat.migration.dto.config.codelist.CSCodelistConfig;
import org.fao.ess.cstat.migration.logic.Command;
import org.fao.ess.cstat.migration.logic.Logic;
import org.fao.ess.cstat.migration.logic.business.codelist.translation.CodelistTranslator;
import org.fao.ess.cstat.migration.utils.json.JSONParser;
import org.fao.ess.cstat.migration.utils.log.CSLogManager;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDCodelist;

import javax.inject.Inject;
import java.io.File;
import java.util.Map;

@Command("codelist")
public class Codelists  implements Logic {

    private static final Logger LOGGER = LogManager.getLogger("Controller");
    private @Inject CSLogManager csLogManager;
    private @Inject InputDao input;
    private @Inject OutputDao output;
    private @Inject CodelistTranslator translator;


    private static final String URL_CODELISTS = "/home/faber-cst/Projects/cstat-migration/config/codelists/old_codelists.json";

    private CSCodelistConfig csCodelists;


    @Override
    public void execute(String... args) throws Exception {

        CSCodelist csCodelist = null;
        csCodelists = JSONParser.toObject(getConfigFile(), CSCodelistConfig.class);


        for(Map<String,String> codelist: csCodelists.getCodelists())
            for(String uid: codelist.keySet()) {
                System.out.println("START processing this codelist: "+uid);

                csCodelist = input.loadCodelist(uid, codelist.get(uid));
                Resource<DSDCodelist, Code> codelistD3S = translator.createCodelist(csCodelist);
                output.storeCodeList(codelistD3S,false);

                System.out.println("stop");

            }

        System.out.println("END");



    }
    private File getConfigFile() {
        return new File(URL_CODELISTS);
    }



}
