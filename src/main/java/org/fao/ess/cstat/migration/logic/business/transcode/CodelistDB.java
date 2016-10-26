package org.fao.ess.cstat.migration.logic.business.transcode;


public enum CodelistDB {

    GAUL(null),
    CS_Units(null),
    CS_Flags(null),
    CS_Prod_Trade(null),
    CS_Indicator("codelist");

    private String dataset;

    CodelistDB(String dataset) {
        this.dataset = dataset;
    }

    public String getDataset() {
        return dataset;
    }

    public static boolean contains(String codelist) {
        for (CodelistDB c : CodelistDB.values())
            if (c.name().equals(codelist))
                return true;
        return false;
    }
}
