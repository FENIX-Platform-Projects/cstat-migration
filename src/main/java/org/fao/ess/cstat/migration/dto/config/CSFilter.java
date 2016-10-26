package org.fao.ess.cstat.migration.dto.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CSFilter {

     @JsonProperty private CSRule codelist;
     @JsonProperty
     private CSRule dataset;

    public CSRule getCodelist() {
        return codelist;
    }

    public void setCodelist(CSRule codelist) {
        this.codelist = codelist;
    }

    public CSRule getDataset() {
        return dataset;
    }

    public void setDataset(CSRule dataset) {
        this.dataset = dataset;
    }
}
