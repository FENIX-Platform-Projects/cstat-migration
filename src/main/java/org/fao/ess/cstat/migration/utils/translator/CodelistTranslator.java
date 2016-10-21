package org.fao.ess.cstat.migration.utils.translator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDCodelist;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.*;

public class CodelistTranslator {

    public Resource<DSDCodelist, Code> createCodelist(JsonNode root) {

        Resource<DSDCodelist, Code> codelistCodeResource = new Resource<DSDCodelist, Code>();

        codelistCodeResource.setMetadata(createMetadata(root));

        if (root.get("rootCodes") == null || !(root.get("rootCodes") instanceof ArrayNode) ||
                ((ArrayNode) root.get("rootCodes")).size() == 0) {
            System.out.println("ERROR: there is not data into the codelsit : " + codelistCodeResource.getMetadata().getUid());
        } else
            codelistCodeResource.setData(addCodes(root.get("rootCodes")));

        return codelistCodeResource;
    }


    private MeIdentification<DSDCodelist> createMetadata(JsonNode codelist) {

        System.out.println("Starting to create the new codelist");
        MeIdentification<DSDCodelist> meIdentification = new MeIdentification<>();
        System.out.println("Creating the new codelist ");

        if (codelist.get("system") != null) {
            //TODO
            System.out.println("ERROR, UID shoudl exists into codelist");
        }
        String uidCodelist = "cstat_" + codelist.get("system").asText();
        String version = codelist.get("version") != null ? codelist.get("version").asText() : null;

        Map<String, String> titles = new HashMap<>();

        if (codelist.get("title") != null && codelist.get("title") instanceof Map)
            titles = (Map<String, String>) codelist.get("title");

        meIdentification.setUid(uidCodelist);
        meIdentification.setVersion(version);
        meIdentification.setTitle(titles);

        return meIdentification;
    }


    private Collection<Code> addCodes(JsonNode rootCodes) {

        Collection<Code> codes = new LinkedList<>();
        for (JsonNode rootCode : rootCodes) {
            Code code = new Code();

            if (rootCode.get("code") == null) {
                System.out.println("ERROR, a code does not exists");
            }
            code.setCode(rootCode.get("code").asText());

            if (rootCode.get("title") == null || !(rootCode.get("title") instanceof Map) ||
                    ((Map) rootCode.get("title")).size() == 0) {
                System.out.println("ERROR, a code does not exists");
            }
            code.setTitle(((Map) rootCode.get("title")));

            Long fromDate = rootCode.get("fromDate") != null && (Object) rootCode.get("fromDate") instanceof Long ? (Long) rootCode.get("fromDate").asLong() : null;
            Long toDate = rootCode.get("toDate") != null && (Object) rootCode.get("toDate") instanceof Long ? (Long) rootCode.get("fromDate").asLong() : null;

            code.setFromDate(fromDate);
            code.setToDate(toDate);

            if (rootCode.get("childs") != null && rootCode.get("childs") instanceof List && ((List) rootCode.get("childs")).size() > 0)
                code.setChildren(addCodes(rootCode.get("childs")));
            codes.add(code);
        }
        return codes;
    }

}
