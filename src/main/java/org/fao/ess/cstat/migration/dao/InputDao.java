package org.fao.ess.cstat.migration.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fao.ess.cstat.migration.dto.cstat.CSDataset;
import org.fao.ess.cstat.migration.rest.RESTClient;
import org.fao.ess.cstat.migration.utils.translator.CodelistTranslator;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDCodelist;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collection;

public class InputDao {

    private @Inject RESTClient client;
    private @Inject CodelistTranslator translator;

    private static final String URL_CODELIST_D3S1 = "http://faostat3.fao.org:7777/msd/cl/system/";
    private static final String URL_METADATA_D3S1 = "http://faostat3.fao.org:7777/msd/dm/";
    private static final String URL_DATA_D3S1 = "http://hqlprfenixapp2.hq.un.fao.org:4242/cstat/dataset/producer/";


    public Resource<DSDCodelist, Code> loadCodelist(String uid, String version) throws Exception {

        JsonNode result = null;
        String finalURL = (version == null) ? URL_CODELIST_D3S1 + uid : URL_CODELIST_D3S1 + uid + "/" + version;
        Response response = client.sendRequest(finalURL, "GET");
        System.out.println("here");
        try {
            result = new ObjectMapper().readValue(response.readEntity(String.class), JsonNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        translator.createCodelist(result);
        System.out.println("JSONNODE");

        return null;
    }


    public CSDataset loadDataset(String uid) throws Exception {

        //TODO: logger system
        System.out.println("PROCESSING THIS UID: " + uid);
        String urlMetadata = URL_METADATA_D3S1 + uid;
        String urlData = URL_DATA_D3S1 + uid;
        CSDataset dataset = new CSDataset();
        Response responseMetadata = client.sendRequest(urlMetadata, "GET");
        Response responseData = client.sendRequest(urlData, "GET");
        try {
            dataset = new ObjectMapper().readValue(responseMetadata.readEntity(String.class), CSDataset.class);
            dataset.setData(new ObjectMapper().readValue(responseData.readEntity(String.class), Collection.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO: logger system
        System.out.println("The Dataset " + uid + " has been setted");
        return dataset;
    }


    Collection<String> getList(String uidRegExp) throws Exception {
        return null;
    }
}
