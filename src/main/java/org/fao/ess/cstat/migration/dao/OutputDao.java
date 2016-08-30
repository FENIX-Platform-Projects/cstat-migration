package org.fao.ess.cstat.migration.dao;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDCodelist;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;

import java.util.Collection;

public class OutputDao {

    Collection<String> getList(String context) throws Exception {
        return null;
    }

    void storeCodeList(Resource<DSDCodelist, Code> codelist) throws Exception {

    }

    void storeDataset(Resource<DSDDataset, Object[]> dataset) throws Exception {

    }

    void updateCodeList(Resource<DSDCodelist, Code> codelist) throws Exception {

    }

    void updateDataset(Resource<DSDDataset, Object[]> dataset) throws Exception {

    }

    void remove(String uid, String version) throws Exception {

    }


}
