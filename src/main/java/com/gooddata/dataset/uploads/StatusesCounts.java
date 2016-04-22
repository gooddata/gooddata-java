package com.gooddata.dataset.uploads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.dataset.UploadStatus;

import org.springframework.web.util.UriTemplate;

import java.util.Map;

//TODO javadoc, check if we can read only map
@JsonTypeName("dataUploadsInfo")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
class StatusesCounts {

    public static final String URI = "/gdc/md/{projectId}/data/uploads_info";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(URI);

    private final Map<UploadStatus, Integer> statusesMap;

    public StatusesCounts(@JsonProperty("statusesCount") Map<UploadStatus, Integer> statusesMap) {
        this.statusesMap = statusesMap;
    }

    public Map<UploadStatus, Integer> getStatusesMap() {
        return statusesMap;
    }
}
