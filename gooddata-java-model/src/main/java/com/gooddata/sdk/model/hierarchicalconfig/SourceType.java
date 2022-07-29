/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.hierarchicalconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Source type defines source of config item and its api uri in case it is manageable.
 * For example, the project can inherit config from the domain, but if the config is defined for both: domain and project,
 * then config from project takes precedence.
 */
public enum SourceType {

    @JsonProperty("catalog")
    CATALOG("catalog", null),
    @JsonProperty("client")
    CLIENT("client", "/gdc/domains/{domainName}/dataproducts/{dataProductId}/clients/{clientId}/config"),
    @JsonProperty("data_product")
    DATA_PRODUCT("data_product", "/gdc/domains/{domainName}/dataproducts/{dataProductId}/config"),
    @JsonProperty("segment")
    SEGMENT("segment", "/gdc/domains/{domainName}/dataproducts/{dataProductId}/segments/{segmentId}/config"),
    @JsonProperty("datawarehouse")
    DATAWAREHOUSE("datawarehouse", null),
    @JsonProperty("domain")
    DOMAIN("domain", "/gdc/domains/{domainName}/config"),
    @JsonProperty("project")
    PROJECT("project", "/gdc/projects/{pid}/config"),
    @JsonProperty("project_group")
    PROJECT_GROUP("project_group", "/gdc/projectGroups/{projectGroup}/config"),
    @JsonProperty("user")
    USER("user", null);

    private static final Map<String, SourceType> lookup = new HashMap<>();

    static {
        for (SourceType c : SourceType.values()) {
            lookup.put(c.name, c);
        }
    }

    /**
     * Name of the source type of config item
     */
    private final String name;

    /**
     * Api uri in case that config item is manageable
     */
    private final String apiUri;

    /**
     * @param name source of config item
     * @param apiUri api uri in case that config item is manageable
     */
    SourceType(String name, String apiUri) {
        this.name = name;
        this.apiUri = apiUri;
    }

    public String getName() {
        return name;
    }

    public String getApiUri() {
        return apiUri;
    }

    /**
     * Get source type by string.
     *
     * @param source name of the source
     * @return Source type enum instance according to the given name
     */
    public static SourceType get(String source) {
        return lookup.get(source);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
