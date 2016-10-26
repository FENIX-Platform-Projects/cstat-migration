package org.fao.ess.cstat.migration.logic.business.validation;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Validator {

    private static final Logger LOGGER = LogManager.getLogger("Validator");

    public void checkIntegrityCostraints (Collection<Object[]> dataNotMatching, Map<String, List<String>> errors, String uid) {

        if(dataNotMatching!= null && dataNotMatching.size()>0) {

            List<String> values = new LinkedList<>();
            StringBuilder stringBuilder = new StringBuilder(" There are some problems with these rows do not respect the integrity :");

            for(Object[] rows: dataNotMatching) {
                stringBuilder.append("\n");
                for (Object cell : rows)
                    stringBuilder.append(cell + " , ");
                stringBuilder.setLength(stringBuilder.length() - 2);

            }

            stringBuilder.setLength(stringBuilder.length() - 2);

            if(errors.containsKey(uid))
                values = errors.get(uid);
            values.add(stringBuilder.toString());
            errors.put(uid,values);
        }
    }



    public void checkCodelistCodes(Collection<Object[]> notMatchingCodes, Map<String, List<String>> errors, String uid, String codelist) {
        System.out.println("here");

        if(notMatchingCodes!= null && notMatchingCodes.size()>0) {

            List<String> values = new LinkedList<>();
            StringBuilder stringBuilder = new StringBuilder(" This codelist "+ codelist+ " does not have these respondent codes into the transcoded file :");

            for(Object[] data: notMatchingCodes)
                stringBuilder.append(data[0]+ " , ");
            stringBuilder.setLength(stringBuilder.length() - 2);

            if(errors.containsKey(uid))
                values = errors.get(uid);
            values.add(stringBuilder.toString());
            errors.put(uid,values);
        }
    }
}
