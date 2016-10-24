package org.fao.ess.cstat.migration.logic.translation;


public enum Subject {

    GEO("geo"),
    UM("um"),
    UNIT("um"),
    VALUE("value"),
    TIME("time");

    private String label;

    Subject(String label){ this.label = label;}

    public String getLabel() {return  label;}

}
