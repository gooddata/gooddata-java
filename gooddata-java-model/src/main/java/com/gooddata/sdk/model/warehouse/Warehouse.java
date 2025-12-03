/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import com.gooddata.sdk.common.util.ISOZonedDateTime;
import com.gooddata.sdk.model.project.Environment;
import com.gooddata.sdk.model.util.UriHelper;

import java.time.ZonedDateTime;
import java.util.Map;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static com.gooddata.sdk.common.util.Validate.notNullState;

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

    private static final String SELF_LINK = "self";
    private static final String STATUS_ENABLED = "ENABLED";
    private final String authorizationToken;
    private String title;
    private String description;
    @ISOZonedDateTime
    private ZonedDateTime created;
    @ISOZonedDateTime
    private ZonedDateTime updated;
    private String createdBy;
    private String updatedBy;
    private String status;
    private String environment;
    private Map<String, String> links;
    private String connectionUrl;
    private String license;

    public Warehouse(String title, String authToken) {
        this(title, authToken, null);
    }

    public Warehouse(String title, String authToken, String description) {
        this.title = notNull(title, "title");
        this.authorizationToken = authToken;
        this.description = description;
    }

    public Warehouse(String title, String authToken, String description, ZonedDateTime created, ZonedDateTime updated,
                     String createdBy, String updatedBy, String status, String environment, String connectionUrl,
                     Map<String, String> links) {
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

    @JsonCreator
    public Warehouse(@JsonProperty("title") String title, @JsonProperty("authorizationToken") String authToken,
                     @JsonProperty("description") String description,
                     @JsonProperty("created") ZonedDateTime created,
                     @JsonProperty("updated") ZonedDateTime updated,
                     @JsonProperty("createdBy") String createdBy, @JsonProperty("updatedBy") String updatedBy,
                     @JsonProperty("status") String status, @JsonProperty("environment") String environment,
                     @JsonProperty("connectionUrl") String connectionUrl,
                     @JsonProperty("links") Map<String, String> links,
                     @JsonProperty("license") String license) {
        this(title, authToken, description, created, updated, createdBy, updatedBy, status, environment, connectionUrl,
                links);
        this.license = license;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the JDBC connection string.
     *
     * @return JDBC connection string
     */
    public String getConnectionUrl() {
        return connectionUrl;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public ZonedDateTime getUpdated() {
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

    public String getLicense() {
        return license;
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
        return UriHelper.getLastUriPart(getUri());
    }

    @JsonIgnore
    public boolean isEnabled() {
        return STATUS_ENABLED.equals(status);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
