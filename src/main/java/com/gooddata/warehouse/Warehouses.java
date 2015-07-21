package com.gooddata.warehouse;

import com.gooddata.collections.PageableList;
import com.gooddata.collections.PageableListSerializer;
import com.gooddata.collections.Paging;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;
import java.util.Map;

/**
 * List of warehouses.
 */
@JsonDeserialize(using = WarehousesDeserializer.class)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(Warehouses.ROOT_NODE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(using = WarehousesSerializer.class)
public class Warehouses extends PageableList<Warehouse> {

    public static final String URI = "/gdc/datawarehouse/instances";

    static final String ROOT_NODE = "instances";

    public Warehouses(final List<Warehouse> items, final Paging paging) {
        super(items, paging);
    }

    public Warehouses(final List<Warehouse> items, final Paging paging, final Map<String, String> links) {
        super(items, paging, links);
    }

}
