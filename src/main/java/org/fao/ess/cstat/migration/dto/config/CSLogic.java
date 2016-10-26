package org.fao.ess.cstat.migration.dto.config;


import com.fasterxml.jackson.annotation.JsonProperty;

public class CSLogic {

     @JsonProperty private boolean overrideCL;
     @JsonProperty private boolean overrideDS;

    public boolean isOverrideCL() {
        return overrideCL;
    }

    public void setOverrideCL(boolean overrideCL) {
        this.overrideCL = (Boolean)overrideCL != null? overrideCL: false;
    }

    public boolean isOverrideDS() {
        return overrideDS;
    }

    public void setOverrideDS(boolean overrideDS) {
        this.overrideDS =  (Boolean)overrideDS != null? overrideDS: false;
    }
}
