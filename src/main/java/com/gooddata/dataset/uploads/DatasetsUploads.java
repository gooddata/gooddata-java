package com.gooddata.dataset.uploads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.web.util.UriTemplate;

import java.util.Collection;

/**
 * Basic information about datasets, dataset uploads, ...
 * Deserialization only. For internal use only
 */
@JsonTypeName("dataSetsInfo")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
class DatasetsUploads {

    static final String URI = "/gdc/md/{projectId}/data/sets";
    static final UriTemplate URI_TEMPLATE = new UriTemplate(URI);

    private final Collection<DatasetUploads> datasets;

    private DatasetsUploads(@JsonProperty("sets") Collection<DatasetUploads> datasets) {
        this.datasets = datasets;
    }

    public Collection<DatasetUploads> getDatasets() {
        return datasets;
    }
}
