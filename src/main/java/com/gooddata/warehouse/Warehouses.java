package com.gooddata.warehouse;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;

import java.util.List;

/**
 * List of warehouses. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("dssInstances")
@JsonIgnoreProperties(ignoreUnknown = true)
class Warehouses {

    static final String URI = "/gdc/dss/instances";

    private final List<Warehouse> items;

    @JsonCreator
    private Warehouses(@JsonProperty("items") List<Warehouse> items) {
        this.items = items;
    }

    List<Warehouse> getItems() {
        return items;
    }
}
