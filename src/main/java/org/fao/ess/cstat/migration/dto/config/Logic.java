package org.fao.ess.cstat.migration.dto.config;


public class Logic {

    private boolean overrideCL;
    private boolean overrideDS;

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
