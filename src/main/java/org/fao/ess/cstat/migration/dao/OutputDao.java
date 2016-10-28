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
    @Inject  RESTClient client;
    private static final String URL_RESOURCES = "http://localhost:7777/v2/msd/resources";

    Collection<String> getList(String context) throws Exception {
        return null;
    }

    public void storeCodeList(Resource<DSDCodelist, Code> codelist, boolean override) throws Exception {
        client.insertCodelist(codelist, override, URL_RESOURCES);

    }

    public boolean storeDataset(Resource<DSDDataset, Object[]> dataset, boolean override, Map<String, List<String>> errors) throws Exception {

         return client.insertResource(dataset, override, errors, URL_RESOURCES);

    }

    void updateCodeList(Resource<DSDCodelist, Code> codelist) throws Exception {

    }

    void updateDataset(Resource<DSDDataset, Object[]> dataset) throws Exception {

    }

    void remove(String uid, String version) throws Exception {

    }


}
