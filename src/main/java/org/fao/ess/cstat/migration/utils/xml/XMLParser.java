package org.fao.ess.cstat.migration.utils.xml;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class XMLParser {

    private final static String URL = "config/datasets/";
    private static final Logger LOGGER = LogManager.getLogger("XMLParser");
    final static Level VERBOSE = Level.getLevel("verbose");
    private static HashSet<String> allowedDomains = new HashSet<>();

    static {
        allowedDomains.add("C");
        //allowedDomains.add("S");
    }


    // get all uid with domain and subdomain
    public static Map<String, Map<String, List<String>>> getAllDatasetFromSchema(String isoCountry) {

        Map<String, Map<String, List<String>>> result = new HashMap<>();

        LOGGER.debug("start to get all data from schema");
        LOGGER.log(VERBOSE, "starting to get all data from schema for country ISO: " + isoCountry);

        try {

            File fXmlFile = new File(URL + isoCountry + "/dataTree.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            if (doc.getDocumentElement().getChildNodes().getLength() <= 0) {
                LOGGER.log(Level.getLevel("ALERT"), "There is a PROBLEM with the configuration of the dataset fot this country");
                LOGGER.error("There is a PROBLEM with the xml configuration for the country");
                throw new Exception();
            }


            NodeList nodeList = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeName() != null && nodeList.item(i).hasAttributes() && nodeList.item(i).getAttributes().getLength() > 0 &&
                        nodeList.item(i).getAttributes().getNamedItem("cod") != null && allowedDomains.contains(nodeList.item(i).getAttributes().getNamedItem("cod").getNodeValue())) {
                    result.put(nodeList.item(i).getAttributes().getNamedItem("cod").getNodeValue(), getChildNodesBySubdomain(nodeList.item(i).getChildNodes()));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.getLevel("ALERT"), "There is a PROBLEM with the configuration of the dataset fot this country");
            e.printStackTrace();
        }

        return result;
    }


    // get all uid with domain
    public static Map<String, List<String>> trasformFlatData(Map<String, Map<String, List<String>>> originalData) {

        Map<String, List<String>> result = new HashMap<>();
        for (String key : originalData.keySet()) {

            List<String> values = new LinkedList<>();
            Map<String, List<String>> tmp = originalData.get(key);
            for (String tmpkey : tmp.keySet()) {
                if (tmp.get(tmpkey).size() > 0)
                    values.addAll(tmp.get(tmpkey));
            }
            result.put(key, values);
        }
        return result;
    }


    // get all uid with domain and restrictions (allowed and denied uid)
    public static Map<String, List<String>> trasformFlatData(Map<String, Map<String, List<String>>> originalData, List<String> denied, List<String> allowed) {

        Map<String, List<String>> result = new HashMap<>();
        for (String key : originalData.keySet()) {

            List<String> values = new LinkedList<>();
            Map<String, List<String>> tmp = originalData.get(key);
            for (String tmpkey : tmp.keySet()) {
                if (tmp.get(tmpkey).size() > 0) {
                    List<String> allowedDatasets = new LinkedList<>();
                    List<String> tmpValues = tmp.get(tmpkey);
                    for (int i = 0; i < tmpValues.size(); i++)
                        if ((denied == null && allowed.contains(tmpValues.get(i))) || (allowed == null && !denied.contains(tmpValues.get(i))))
                            allowedDatasets.add(tmpValues.get(i));
                    values.addAll(allowedDatasets);

                }
            }
            result.put(key, values);
        }
        return result;
    }


    // get all uid with restriction based on regexp(TODO)
    public List<String> filterDataOnRegexp(String regExp, List<String> data) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).matches(regExp)) { // matches uses regex
                // TODO: ADD into the logger
                System.out.println("The uid " + data.get(i) + " matches the regexp " + regExp);
                result.add(data.get(i));
            }
        }
        return result;

    }


    // Utils
    private static Map<String, List<String>> getChildNodesBySubdomain(NodeList nodeList) {

        Map<String, List<String>> result = new HashMap<>();
        for (int j = 0; j < nodeList.getLength(); j++) {
            // there should be a code attribute into the node
            if (nodeList.item(j) != null && nodeList.item(j).hasAttributes() && nodeList.item(j).getAttributes().getLength() > 0 &&
                    nodeList.item(j).getAttributes().getNamedItem("cod") != null
                    && nodeList.item(j).hasChildNodes() && nodeList.item(j).getChildNodes().getLength() > 0) {

                // ONLY TRADE datasets
                if (nodeList.item(j).getAttributes().getNamedItem("cod").getNodeValue().equals("CPD")) {
                    System.out.println("hh");
                    result.put(nodeList.item(j).getAttributes().getNamedItem("cod").getNodeValue(), getDatasets(nodeList.item(j).getChildNodes()));

                }

            }
        }

        return result;

    }

    private static List<String> getDatasets(NodeList nodeList) {

        List<String> result = new LinkedList<>();
        for (int k = 0; k < nodeList.getLength(); k++) {
            if (nodeList.item(k) != null && nodeList.item(k).hasAttributes() && nodeList.item(k).getAttributes().getLength() > 0 &&
                    nodeList.item(k).getAttributes().getNamedItem("file") != null) {

                result.add(nodeList.item(k).getAttributes().getNamedItem("file").getNodeValue());
            }
        }
        return result;
    }

}
