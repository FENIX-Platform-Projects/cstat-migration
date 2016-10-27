package org.fao.ess.cstat.migration.dao;

import org.fao.ess.cstat.migration.db.rest.RESTClient;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDCodelist;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class OutputDao {
    @Inject
    RESTClient client;

    Collection<String> getList(String context) throws Exception {
        return null;
    }

    void storeCodeList(Resource<DSDCodelist, Code> codelist) throws Exception {

    }

    public void storeDataset(Resource<DSDDataset, Object[]> dataset, boolean override, Map<String, List<String>> errors) throws Exception {

        client.insertResource(dataset, override, errors);

    }

    void updateCodeList(Resource<DSDCodelist, Code> codelist) throws Exception {

    }

    void updateDataset(Resource<DSDDataset, Object[]> dataset) throws Exception {

    }

    void remove(String uid, String version) throws Exception {

    }


}
