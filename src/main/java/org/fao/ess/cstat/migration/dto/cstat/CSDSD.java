package org.fao.ess.cstat.migration.dto.cstat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;


@JsonIgnoreProperties(ignoreUnknown = true)
public class CSDSD {

    @JsonProperty
    private Collection<CSColumn> columns;

    public Collection<CSColumn> getColumns() {
        return columns;
    }

    public void setColumns(Collection<CSColumn> columns) {
        this.columns = columns;
    }
}
