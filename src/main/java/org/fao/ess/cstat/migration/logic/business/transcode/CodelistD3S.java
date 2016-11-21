package org.fao.ess.cstat.migration.logic.business.transcode;


public enum CodelistD3S {

    CS_Machinery(new String[]{"HS", "full"}),
    CS_Prod_Trade(new String[]{"HS", "full"}),
    CS_Population(new String[]{"CS_Population", "2014"}),
    CS_Prices(new String[]{"CS_Prices", "2014"}),
    CS_Fertilizers(new String[]{"HS", "full"}),
    CS_Fisheries(new String[]{"CS_Fisheries", "2014"}),
    CS_Labour(new String[]{"CS_Labour", "2014"}),
    CS_Land(new String[]{"CS_Land", "2014"}),
    CS_ValueAdded(new String[]{"CS_ValueAdded", "2014"}),
    CS_Water(new String[]{"CS_Water", "2014"}),
    CS_Forestry(new String[]{"CS_Forestry", "2014"}),
    CS_CPI(new String[]{"CS_CPI", "2014"}),
    CS_Pesticides(new String[]{"HS", "full"}),
    CS_PPI(new String[]{"CS_PPI", "2014"}),
    CS_FoodSupply(new String[]{"CS_FoodSupply", "2014"}),
    HS(new String[]{"HS", "full"}),
    GAUL(new String[]{"GAUL", "2014"}),
    CS_Indicator(new String[]{"CountrySTAT_Indicators", null}),
    CS_Units(new String[]{"CountrySTAT_UM", null}),
    CS_Flags(new String[]{"Flag", "1.0"}),
    FAOSTAT_CoveregeSectors(new String[]{"FAOSTAT_CoveregeSectors", "1.0"});

    private String[] codelist;

    CodelistD3S(String[] codelist) {
        this.codelist = codelist;
    }

    public String getCodelistID() {
        return codelist[0];
    }

    public String getVersion() {
        return codelist[1];
    }
}
