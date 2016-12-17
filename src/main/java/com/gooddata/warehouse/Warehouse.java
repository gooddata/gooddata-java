/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.warehouse;

import static com.gooddata.util.Validate.notNull;
import static com.gooddata.util.Validate.notNullState;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.project.Environment;
import com.gooddata.util.ISODateTimeDeserializer;
import com.gooddata.util.ISODateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

/**
 * Warehouse
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("instance")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Warehouse {

    private static final String ID_PARAM = "id";
    public static final String URI = Warehouses.URI + "/{" + ID_PARAM + "}";

    public static final UriTemplate TEMPLATE = new UriTemplate(URI);
    public static final UriTemplate JDBC_CONNECTION_TEMPLATE = new UriTemplate("jdbc:gdc:datawarehouse://{host}:{port}/gdc/datawarehouse/instances/{id}");

    private static final String SELF_LINK = "self";
    private static final String STATUS_ENABLED = "ENABLED";

    private String title;
    private String description;

    private final String authorizationToken;
    private DateTime created;
    private DateTime updated;
    private String createdBy;
    private String updatedBy;
    private String status;
    private String environment;
    private Map<String, String> links;
    private String connectionUrl;

    public Warehouse(String title, String authToken) {
        this(title, authToken, null);
    }

    public Warehouse(String title, String authToken, String description) {
        this.title = notNull(title, "title");
        this.authorizationToken = authToken;
        this.description = description;
    }

    @JsonCreator
    public Warehouse(@JsonProperty("title") String title, @JsonProperty("authorizationToken") String authToken,
                     @JsonProperty("description") String description,
                     @JsonProperty("created") @JsonDeserialize(using = ISODateTimeDeserializer.class) DateTime created,
                     @JsonProperty("updated") @JsonDeserialize(using = ISODateTimeDeserializer.class) DateTime updated,
                     @JsonProperty("createdBy") String createdBy, @JsonProperty("updatedBy") String updatedBy,
                     @JsonProperty("status") String status, @JsonProperty("environment") String environment,
                     @JsonProperty("connectionUrl") String connectionUrl,
                     @JsonProperty("links") Map<String, String> links) {
        this(title, authToken, description);
        this.created = created;
        this.updated = updated;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.status = status;
        this.environment = environment;
        this.connectionUrl = connectionUrl;
        this.links = links;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Gets the JDBC connection string.
     *
     * @return JDBC connection string
     */
    public String getConnectionUrl() { return connectionUrl; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonSerialize(using = ISODateTimeSerializer.class)
    public DateTime getCreated() {
        return created;
    }

    @JsonSerialize(using = ISODateTimeSerializer.class)
    public DateTime getUpdated() {
        return updated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getStatus() {
        return status;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(final String environment) {
        this.environment = environment;
    }

    @JsonIgnore
    public void setEnvironment(final Environment environment) {
        notNull(environment, "environment");
        setEnvironment(environment.name());
    }

    public Map<String, String> getLinks() {
        return links;
    }

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").get(SELF_LINK);
    }

    @JsonIgnore
    public String getId() {
        return TEMPLATE.match(getUri()).get(ID_PARAM);
    }

    @JsonIgnore
    public boolean isEnabled() {
        return STATUS_ENABLED.equals(status);
    }
}
