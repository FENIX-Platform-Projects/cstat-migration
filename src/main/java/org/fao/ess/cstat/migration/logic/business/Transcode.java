package org.fao.ess.cstat.migration.logic.business;


public enum Transcode {

    GAUL(null),
    CS_Units(null),
    CS_Flags(null),
    CS_Prod_Trade(null),
    CS_Indicator("codelist");

    private String dataset;

    Transcode(String dataset) {
        this.dataset = dataset;
    }

    public String getDataset() {
        return dataset;
    }
}
