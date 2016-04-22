package com.gooddata.dataset.uploads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collection;

//TODO javadoc, other things
@JsonTypeName("dataUploads")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
class Uploads {

    private Collection<Upload> uploads;

    public Uploads(@JsonProperty("uploads") Collection<Upload> uploads) {
        this.uploads = uploads;
    }

    public Collection<Upload> getUploads() {
        return uploads;
    }
}
