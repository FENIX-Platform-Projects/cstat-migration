package org.fao.ess.cstat.migration.dto.config.dataset;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CSConfig {

    @JsonProperty private String country;
    @JsonProperty private CSLogic logics;
    @JsonProperty private CSFilter filters;
    @JsonProperty private String language;
    @JsonProperty private String[] datasetsToFill;
    @JsonProperty private boolean forceDatasets;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public CSLogic getLogics() {
        return logics;
    }

    public void setLogics(CSLogic logics) {
        this.logics = logics;
    }

    public CSFilter getFilters() {
        return filters;
    }

    public void setFilters(CSFilter filters) {
        this.filters = filters;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String[] getDatasetsToFill() {
        return datasetsToFill;
    }

    public void setDatasetsToFill(String[] datasetsToFill) {
        this.datasetsToFill = datasetsToFill;
    }

    public boolean isForceDatasets() {
        return forceDatasets;
    }

    public void setForceDatasets(boolean forceDatasets) {
        this.forceDatasets = forceDatasets;
    }
}
