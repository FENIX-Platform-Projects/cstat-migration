package org.fao.ess.cstat.migration.dto.subjects;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubjectTitle {

      @JsonProperty private HashMap<String,String> um;
      @JsonProperty private HashMap<String,String> value;
      @JsonProperty private HashMap<String,String> item;
      @JsonProperty private HashMap<String,String> indicator;
      @JsonProperty private HashMap<String,String> flag;
      @JsonProperty private HashMap<String,String> time;
      @JsonProperty private HashMap<String,HashMap<String,String>> geo;

    public HashMap<String, String> getUm() {
        return um;
    }

    public void setUm(HashMap<String, String> um) {
        this.um = um;
    }

    public HashMap<String, String> getValue() {
        return value;
    }

    public void setValue(HashMap<String, String> value) {
        this.value = value;
    }

    public HashMap<String, String> getItem() {
        return item;
    }

    public void setItem(HashMap<String, String> item) {
        this.item = item;
    }

    public HashMap<String, String> getIndicator() {
        return indicator;
    }

    public void setIndicator(HashMap<String, String> indicator) {
        this.indicator = indicator;
    }

    public HashMap<String, String> getFlag() {
        return flag;
    }

    public void setFlag(HashMap<String, String> flag) {
        this.flag = flag;
    }

    public HashMap<String, String> getTime() {
        return time;
    }

    public void setTime(HashMap<String, String> time) {
        this.time = time;
    }

    public HashMap<String, HashMap<String, String>> getGeo() {
        return geo;
    }

    public void setGeo(HashMap<String, HashMap<String, String>> geo) {
        this.geo = geo;
    }
}
