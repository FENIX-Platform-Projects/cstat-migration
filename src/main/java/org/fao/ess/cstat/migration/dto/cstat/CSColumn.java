package org.fao.ess.cstat.migration.dto.cstat;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CSColumn {

    @JsonProperty private String columnId;
    @JsonProperty private String dataType;
    @JsonProperty private Map<String,String> title;
    @JsonProperty private CSDimension dimension;
    @JsonProperty private CSCodeSystem codeSystem;
    @JsonProperty private Collection<CSValue> valuesCountrystat;

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public CSDimension getDimension() {
        return dimension;
    }

    public void setDimension(CSDimension dimension) {
        this.dimension = dimension;
    }

    public CSCodeSystem getCodeSystem() {
        return codeSystem;
    }

    public void setCodeSystem(CSCodeSystem codeSystem) {
        this.codeSystem = codeSystem;
    }

    public Collection<CSValue> getValuesCountrystat() {
        return valuesCountrystat;
    }

    public void setValuesCountrystat(Collection<CSValue> valuesCountrystat) {
        this.valuesCountrystat = valuesCountrystat;
    }
}
