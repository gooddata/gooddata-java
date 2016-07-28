package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collection;

/**
 * Contains collection of dataset uploads.
 * Deserialization only. For internal use only.
 */
@JsonTypeName("dataUploads")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
class Uploads {

    private Collection<Upload> uploads;

    Uploads(@JsonProperty("uploads") Collection<Upload> uploads) {
        this.uploads = uploads;
    }

    /**
     * @return all items of uploads collection
     */
    Collection<Upload> items() {
        return uploads;
    }
}
