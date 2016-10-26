package org.fao.ess.cstat.migration.dto.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CSRule {

     @JsonProperty private List<String> includedID;
     @JsonProperty private List<String> excludedID;


     public List<String> getIncludedID() {
          return includedID;
     }

     public void setIncludedID(List<String> includedID) {
          this.includedID = includedID;
     }

     public List<String> getExcludedID() {
          return excludedID;
     }

     public void setExcludedID(List<String> excludedID) {
          this.excludedID = excludedID;
     }
}
