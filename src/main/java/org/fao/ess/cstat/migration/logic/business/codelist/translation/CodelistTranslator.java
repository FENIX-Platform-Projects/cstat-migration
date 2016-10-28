package org.fao.ess.cstat.migration.logic.business.codelist.translation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.log4j.Logger;
import org.fao.ess.cstat.migration.dto.codelist.CSCodelist;
import org.fao.ess.cstat.migration.dto.config.codelist.CSCode;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDCodelist;
import org.fao.fenix.commons.msd.dto.full.MeContent;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;

import java.util.*;

public class CodelistTranslator {

    public Resource<DSDCodelist, Code> createCodelist(CSCodelist root) throws Exception {

        Resource<DSDCodelist, Code> codelistCodeResource = new Resource<DSDCodelist, Code>();
        try {
            validate(root);
            codelistCodeResource.setMetadata(createMetadata(root));
            codelistCodeResource.setData(addCodes(root.getRootCodes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codelistCodeResource;
    }


    private MeIdentification<DSDCodelist> createMetadata(CSCodelist codelist) {

        MeIdentification<DSDCodelist> meIdentification = new MeIdentification<>();
        meIdentification.setUid(codelist.getSystem());
        meIdentification.setVersion(codelist.getSystem());
        meIdentification.setTitle(codelist.getTitle());
        MeContent meContent = new MeContent();
        meContent.setResourceRepresentationType(RepresentationType.codelist);
        meIdentification.setMeContent(meContent);

        return meIdentification;
    }


    private Collection<Code> addCodes(List<CSCode> csCodes) {

        Collection<Code> codes = new LinkedList<>();
        for (CSCode csCode : csCodes) {
            Code code = new Code();
            code.setCode(csCode.getCode());
            code.setTitle(csCode.getTitle());
            if (csCode.getChilds() != null && csCode.getChilds().size() > 0)
                code.setChildren(addCodes(csCode.getChilds()));
            codes.add(code);
        }
        return codes;
    }


    private void validate(CSCodelist csCodelist) throws Exception {

        String error = null;
        if (csCodelist.getSystem() == null)
            error = "id does not exists";

        if (csCodelist.getTitle() == null)
            error = "title does not exists";

        if (csCodelist.getVersion() == null)
            error = "version does not exists";

        if (csCodelist.getRootCodes() == null)
            error = "data do not exist";
        if (csCodelist.getRootCodes() != null && csCodelist.getRootCodes().size() == 0)
            error = "data do not exist";

        if (error != null)
            throw new Exception(error);
    }

}
