package org.fao.ess.cstat.migration.dto.codelist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.fao.ess.cstat.migration.dto.config.codelist.CSCode;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CSCodelist {

    @JsonProperty private String system;
    @JsonProperty private String version;
    @JsonProperty private Map<String, String> title;
    @JsonProperty private Map<String, String> description;
    @JsonProperty private List<CSCode> rootCodes;

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public List<CSCode> getRootCodes() {
        return rootCodes;
    }

    public void setRootCodes(List<CSCode> rootCodes) {
        this.rootCodes = rootCodes;
    }
}
