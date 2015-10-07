package com.gooddata.warehouse;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;

import java.util.Map;

/**
 * Async task for warehouse. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("asyncTask")
@JsonIgnoreProperties(ignoreUnknown = true)
class WarehouseTask {

    private static final String POLL_LINK = "poll";
    private static final String WAREHOUSE_LINK = "instance";
    private static final String WAREHOUSE_USER_LINK = "user";

    private final Map<String,String> links;

    @JsonCreator
    private WarehouseTask(@JsonProperty("links") Map<String, String> links) {
        this.links = links;
    }

    String getPollLink() {
        return links.get(POLL_LINK);
    }

    String getWarehouseLink() {
        return links.get(WAREHOUSE_LINK);
    }

    String getWarehouseUserLink() {
        return links.get(WAREHOUSE_USER_LINK);
    }
}
