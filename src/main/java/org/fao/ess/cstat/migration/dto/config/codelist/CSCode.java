package org.fao.ess.cstat.migration.dto.config.codelist;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
@JsonIgnoreProperties(ignoreUnknown = true)
public class CSCode {

    @JsonProperty private String code;
    @JsonProperty private Map<String,String> title;
    @JsonProperty private List<CSCode> childs;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public List<CSCode> getChilds() {
        return childs;
    }

    public void setChilds(List<CSCode> childs) {
        this.childs = childs;
    }
}
