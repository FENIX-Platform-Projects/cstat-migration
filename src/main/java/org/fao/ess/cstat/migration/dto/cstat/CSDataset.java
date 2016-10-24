package org.fao.ess.cstat.migration.dto.cstat;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.fao.ess.cstat.migration.dto.cstat.CSDSD;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
@JsonIgnoreProperties(ignoreUnknown = true)
public class CSDataset {

    @JsonProperty private String uid;
    @JsonProperty private Map<String,String> title;
    @JsonProperty private Date creationDate;
    @JsonProperty private Date updateDate;
    @JsonProperty private CSDSD dsd;
    @JsonProperty private Collection<Object> data;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }


    public Collection<Object> getData() {
        return data;
    }

    public void setData(Collection<Object> data) {
        this.data = data;
    }

    public CSDSD getDsd() {
        return dsd;
    }

    public void setDsd(CSDSD dsd) {
        this.dsd = dsd;
    }
}
