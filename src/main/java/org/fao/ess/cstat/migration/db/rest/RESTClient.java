package org.fao.ess.cstat.migration.db.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fao.fenix.commons.find.dto.filter.FieldFilter;
import org.fao.fenix.commons.find.dto.filter.IdFilter;
import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.fenix.commons.msd.dto.data.ReplicationFilter;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.*;

@ApplicationScoped
public class RESTClient {

    public Collection<MeIdentification<DSDDataset>> retrieveMetadata(String baseUrl) throws Exception {
        //Create filter
        StandardFilter filter = new StandardFilter();

        FieldFilter fieldFilter = new FieldFilter();
        fieldFilter.enumeration = Arrays.asList("oecd");
        filter.put("dsd.contextSystem", fieldFilter);

        fieldFilter = new FieldFilter();
        fieldFilter.enumeration = Arrays.asList("dataset");
        filter.put("meContent.resourceRepresentationType", fieldFilter);

        //Send request
        Map<String,String> parameters = new HashMap<>();
        parameters.put("maxSize","1000000");
        String url = addQueryParameters(baseUrl+"msd/resources/find", parameters);
        Response response = sendRequest(url, filter, "post");
        if (response.getStatus() != 200 && response.getStatus() != 201 && response.getStatus() != 204)
            throw new Exception("Error from D3S requiring existing datasets metadata");

        //Parse response
        return response.getStatus()!=204 ? response.readEntity(new GenericType<Collection<MeIdentification<DSDDataset>>>(){}) : new LinkedList<MeIdentification<DSDDataset>>();
    }

    public void insertMetadata (String baseUrl, Collection<MeIdentification<DSDDataset>> metadataList) throws Exception {
        if (metadataList==null || metadataList.size()==0)
            return;
        //Send requests
        for (Collection<MeIdentification<DSDDataset>> segment : splitCollection(metadataList,25)) {
            Response response = sendRequest(baseUrl + "msd/resources/massive", segment, "post");
            if (response.getStatus() != 200 && response.getStatus() != 201)
                throw new Exception("Error from D3S adding datasets metadata");
        }
    }

    public void updateMetadata (String baseUrl, Collection<MeIdentification<DSDDataset>> metadataList) throws Exception {
        if (metadataList==null || metadataList.size()==0)
            return;
        //Send request
        for (Collection<MeIdentification<DSDDataset>> segment : splitCollection(metadataList,25)) {
            Response response = sendRequest(baseUrl + "msd/resources/massive", segment, "put");
            if (response.getStatus() != 200 && response.getStatus() != 201)
                throw new Exception("Error from D3S adding datasets metadata");
        }
    }

    public void deleteMetadata (String baseUrl, Collection<MeIdentification<DSDDataset>> metadataList) throws Exception {
        if (metadataList==null || metadataList.size()==0)
            return;

        //Create filter
        FieldFilter fieldFilter = new FieldFilter();
        fieldFilter.ids = new LinkedList<>();
        for (MeIdentification<DSDDataset> metadata : metadataList)
            fieldFilter.ids.add(new IdFilter(metadata.getUid(), metadata.getVersion()));
        StandardFilter filter = new StandardFilter();
        filter.put("id", fieldFilter);

        //Send request
        Response response = sendRequest(baseUrl+"msd/resources/massive/delete", filter, "post");
        if (response.getStatus() != 200 && response.getStatus() != 201)
            throw new Exception("Error from D3S requiring existing datasets metadata");
    }

    public void updateCodelists (String baseUrl, Collection<Resource<DSDCodelist, Code>> resourceList) throws Exception {
        if (resourceList==null || resourceList.size()==0)
            return;
        //Send request
        for (Resource<DSDCodelist, Code> resource : resourceList) {
            Response response = sendRequest(baseUrl + "msd/resources", resource, "put");
            if (response.getStatus() != 200 && response.getStatus() != 201)
                throw new Exception("Error from D3S updating codelist "+resource.getMetadata().getUid());
        }
    }
    public void updateDatasetMetadataUpdateDate (String baseUrl, String contextSystem) throws Exception {
        if (contextSystem==null)
            return;
        //Create filter
        StandardFilter filter = new StandardFilter();

        FieldFilter fieldFilter = new FieldFilter();
        fieldFilter.enumeration = Arrays.asList(contextSystem);
        filter.put("dsd.contextSystem", fieldFilter);

        fieldFilter = new FieldFilter();
        fieldFilter.enumeration = Arrays.asList("dataset");
        filter.put("meContent.resourceRepresentationType", fieldFilter);

        MeIdentification<DSDDataset> metadata = new MeIdentification<>();
        MeMaintenance meMaintenance = new MeMaintenance();
        metadata.setMeMaintenance(meMaintenance);
        SeUpdate seUpdate = new SeUpdate();
        seUpdate.setUpdateDate(new Date());
        meMaintenance.setSeUpdate(seUpdate);

        ReplicationFilter<DSDDataset> updateFilter = new ReplicationFilter<>();
        updateFilter.setFilter(filter);
        updateFilter.setMetadata(metadata);

        //Send request
        Response response = sendRequest(baseUrl+"msd/resources/replication", updateFilter, "patch");
        if (response.getStatus() != 200 && response.getStatus() != 201 && response.getStatus() != 204)
            throw new Exception("Error from D3S updating datasets metadata last update date");
    }

    public Response sendRequest(String url, String method) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        return target.request(MediaType.APPLICATION_JSON_TYPE).build(method.trim().toUpperCase()).invoke();
    }

    private String addQueryParameters (String url, Map<String,String> parameters) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(url);
        if (parameters!=null && parameters.size()>0) {
            sb.append('?');
            for (Map.Entry<String, String> parameter : parameters.entrySet())
                sb.append(URLEncoder.encode(parameter.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(parameter.getValue(), "UTF-8")).append('&');
            sb.setLength(sb.length()-1);
        }
        return sb.toString();
    }
    public Response sendRequest(String url, Object entity, String method) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        return target.request(MediaType.APPLICATION_JSON_TYPE).build(method.trim().toUpperCase(), javax.ws.rs.client.Entity.json(entity)).invoke();
    }


    private <T> Collection<Collection<T>>  splitCollection(Collection<T> list, int segmentSize) {
        if (list==null)
            return null;
        Collection<Collection<T>> buffer = new LinkedList<>();
        Collection<T> segment = new LinkedList<>();
        int count = 0;
        for (T element : list) {
            if (++count>segmentSize) {
                buffer.add(segment);
                segment = new LinkedList<>();
                count=0;
            }
            segment.add(element);
        }
        if (segment.size()>0)
            buffer.add(segment);
        return buffer;
    }


    public void insertResource(Resource<DSDDataset, Object[]> resource, boolean override, Map<String, List<String>> errors) {
        try {

            ClientRequest request = new ClientRequest(
                    "http://localhost:7777/v2/msd/resources");
            request.accept("application/json");

            ObjectMapper mapper = new ObjectMapper();
            String input = mapper.writeValueAsString(resource);

            request.body("application/json", input);

            ClientResponse<String> response = (override)?request.put(String.class):request.post(String.class);

            if (response.getStatus() != 201 && response.getStatus() != 200) {
                handleErrors(errors,resource.getMetadata().getUid(),"This dataset could not be saved in the server; the response of the D3S is "+response.getStatus());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(response.getEntity().getBytes())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


    private void handleErrors (Map<String, List<String>> errors, String uid, String messageError) {
        List<String> values = new LinkedList<>();
        if(errors.containsKey(uid))
            values = errors.get(uid);
        values.add(messageError);
        errors.put(uid,values);
    }
}
