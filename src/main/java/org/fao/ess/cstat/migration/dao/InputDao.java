package org.fao.ess.cstat.migration.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fao.ess.cstat.migration.dto.codelist.CSCodelist;
import org.fao.ess.cstat.migration.dto.cstat.CSDataset;
import org.fao.ess.cstat.migration.db.rest.RESTClient;
import org.fao.ess.cstat.migration.utils.log.CSLogManager;
import org.fao.ess.cstat.migration.logic.business.codelist.translation.CodelistTranslator;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InputDao {

    private static final Logger LOGGER = LogManager.getLogger("InputDAO");
    private @Inject CSLogManager csLogManager;
    private @Inject RESTClient client;
    private @Inject CodelistTranslator translator;

    private static final String URL_CODELIST_D3S1 = "http://faostat3.fao.org:7777/msd/cl/system/";
    private static final String URL_METADATA_D3S1 = "http://faostat3.fao.org:7777/msd/dm/";
    private static final String URL_DATA_D3S1 = "http://hqlprfenixapp2.hq.un.fao.org:4242/cstat/dataset/producer/";


    public CSCodelist loadCodelist(String uid, String version) throws Exception {

        CSCodelist result = null;
        String finalURL = (version == null) ? URL_CODELIST_D3S1 + uid : URL_CODELIST_D3S1 + uid + "/" + version;
        Response response = client.sendRequest(finalURL, "GET");
        try {
            result = new ObjectMapper().readValue(response.readEntity(String.class), CSCodelist.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    public CSDataset loadDataset(String uid, Map<String, List<String>> errors ) throws Exception {

        String urlMetadata = URL_METADATA_D3S1 + uid;
        String urlData = URL_DATA_D3S1 + uid;
        CSDataset dataset = new CSDataset();
        Response responseMetadata = client.sendRequest(urlMetadata, "GET");

        if(responseMetadata.getStatus() != 200)
            csLogManager.writeInternalMessage(LOGGER,3,"The response of the server for the METADAT of this dataset is " + responseMetadata.getStatus());

        if(responseMetadata.getStatus() == 200) {
            Response responseData = client.sendRequest(urlData, "GET");

            if (responseData.getStatus() != 200)
                csLogManager.writeInternalMessage(LOGGER,3, "The response of the server for the DATA of this dataset is " + responseData.getStatus());

            if (responseData.getStatus() == 200 && responseMetadata.getStatus() == 200) {

                try {
                    dataset = new ObjectMapper().readValue(responseMetadata.readEntity(String.class), CSDataset.class);
                    dataset.setData(new ObjectMapper().readValue(responseData.readEntity(String.class), Collection.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                csLogManager.writeInternalMessage(LOGGER,1,"The Dataset " + uid + " has been setted");
            }
        }
        return dataset;
    }


    Collection<String> getList(String uidRegExp) throws Exception {
        return null;
    }


    private void handleErrors (Map<String, List<String>> errors, String uid, String messageError) {
        List<String> values = new LinkedList<>();
        if(errors.containsKey(uid))
            values = errors.get(uid);
        values.add(messageError);
        errors.put(uid,values);
    }

}
