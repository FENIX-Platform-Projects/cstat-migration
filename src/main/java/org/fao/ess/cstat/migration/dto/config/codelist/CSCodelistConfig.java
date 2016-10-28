package org.fao.ess.cstat.migration.dto.config.codelist;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class CSCodelistConfig {

    @JsonProperty private List<Map<String,String>> codelists;

    public List<Map<String, String>> getCodelists() {
        return codelists;
    }

    public void setCodelists(List<Map<String, String>> codelists) {
        this.codelists = codelists;
    }
}
