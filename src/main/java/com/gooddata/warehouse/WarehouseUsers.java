package com.gooddata.warehouse;

import com.gooddata.collections.PageableList;
import com.gooddata.collections.Paging;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Map;

/**
 * List of warehouse users.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(WarehouseUsers.ROOT_NODE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = WarehouseUsersDeserializer.class)
@JsonSerialize(using = WarehouseUsersSerializer.class)
public class WarehouseUsers extends PageableList<WarehouseUser> {

    public static final String URI = Warehouse.URI + "/users";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    static final String ROOT_NODE = "users";

    public WarehouseUsers(final List<WarehouseUser> items, final Paging paging) {
        super(items, paging);
    }

    public WarehouseUsers(final List<WarehouseUser> items, final Paging paging, final Map<String, String> links) {
        super(items, paging, links);
    }

}
