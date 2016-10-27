package org.fao.ess.cstat.migration.logic.business.validation;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Validator {

    private static final Logger LOGGER = LogManager.getLogger("Validator");

    public void checkIntegrityCostraints(Collection<Object[]> dataNotMatching, Map<String, List<String>> errors, String uid, Set<String> keyColumns) {

        if(dataNotMatching!= null && dataNotMatching.size()>0) {

            List<String> values = new LinkedList<>();
            StringBuilder stringBuilder = new StringBuilder("There are some problems with these rows do not respect the integrity :");
            stringBuilder.append("\n");

            // columns
            for(String s: keyColumns)
                stringBuilder.append(s + " , ");
            stringBuilder.setLength(stringBuilder.length() - 2);

            // data
            for(Object[] rows: dataNotMatching) {
                stringBuilder.append("\n");
                for (Object cell : rows)
                    stringBuilder.append(cell + " , ");
                stringBuilder.setLength(stringBuilder.length() - 2);
            }
            stringBuilder.setLength(stringBuilder.length() - 2);

            handleErrors(uid,stringBuilder.toString(),errors);

        }
    }



    public void checkCodelistCodes(Collection<Object[]> notMatchingCodes, Map<String, List<String>> errors, String uid, String codelist) {

        if(notMatchingCodes!= null && notMatchingCodes.size()>0) {

            StringBuilder stringBuilder = new StringBuilder("The codelist "+ codelist+ " does not have these codes in the transcoded file :");

            stringBuilder.append("\n");
            for(Object[] data: notMatchingCodes) {
                stringBuilder.append(data[0]);
                stringBuilder.append("\n");
            }
            handleErrors(uid,stringBuilder.toString(),errors);
        }
    }


    private void handleErrors (String uid, String message, Map<String, List<String>> errors) {
        List<String> values = new LinkedList<>();

        if(errors.containsKey(uid))
            values = errors.get(uid);
        values.add(message);
        errors.put(uid,values);

    }
}
