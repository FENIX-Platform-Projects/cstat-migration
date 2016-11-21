package org.fao.ess.cstat.migration.logic.business.transcode;


public enum CodelistDB {

    GAUL(null),
    CS_Units("um"),
    CS_Flags("flags"),
    CS_Prod_Trade("prod_trade"),
    CS_Indicator("trade"),
    CS_Machinery("codelist"),
    CS_Population("codelist"),
    CS_Prices("codelist"),
    CS_Fertilizers("codelist"),
    CS_Fisheries("codelist"),
    CS_Labour("codelist"),
    CS_Land("codelist"),
    CS_ValueAdded("codelist"),
    CS_Water("codelist"),
    CS_Forestry("codelist"),
    CS_CPI("codelist"),
    CS_Pesticides("codelist"),
    CS_PPI("codelist"),
    CS_FoodSupply("codelist"),
    HS(null),
    FAOSTAT_CoveregeSectors("codelist");

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
