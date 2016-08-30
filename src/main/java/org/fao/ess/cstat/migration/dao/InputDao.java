package org.fao.ess.cstat.migration.dao;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDCodelist;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;

import java.util.Collection;

public class InputDao {

    Resource<DSDCodelist, Code> loadCodelist(String uid, String version) throws Exception {
        return null;
    }

    Resource<DSDDataset, Object[]> loadDataset (String uid) throws Exception {
        return null;
    }

    Collection<String> getList (String uidRegExp) throws Exception {
        return null;
    }
}
