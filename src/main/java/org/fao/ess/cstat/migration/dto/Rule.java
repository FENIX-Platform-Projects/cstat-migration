package org.fao.ess.cstat.migration.dto;

public class Rule {

    private String[] includedID;
    private String[] excludedID;


    public String[] getIncludedID() {
        return includedID;
    }

    public void setIncludedID(String[] includedID) {
        this.includedID = includedID;
    }

    public String[] getExcludedID() {
        return excludedID;
    }

    public void setExcludedID(String[] excludedID) {
        this.excludedID = excludedID;
    }
}
