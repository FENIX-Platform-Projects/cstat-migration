package org.fao.ess.cstat.migration.dto;


public class Config {

    private String country;
    private Logic logics;
    private Filter filters;


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Logic getLogics() {
        return logics;
    }

    public void setLogics(Logic logics) {
        this.logics = logics;
    }

    public Filter getFilters() {
        return filters;
    }

    public void setFilters(Filter filters) {
        this.filters = filters;
    }


}
