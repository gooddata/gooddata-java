/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.warehouse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.account.Account;
import com.gooddata.util.GoodDataToStringBuilder;
import com.gooddata.util.ISODateTimeDeserializer;
import com.gooddata.util.ISODateTimeSerializer;
import org.joda.time.DateTime;
import org.springframework.web.util.UriTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.gooddata.util.Validate.notNullState;

/**
 * Single warehouse S3 credentials record (identified uniquely by warehouse ID, region and access key)
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("s3Credentials")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseS3Credentials {
    public static final String URI = WarehouseS3CredentialsList.URI + "/{region}/{accessKey}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    private static final String SELF_LINK = "self";
    private static final String INSTANCE_LINK = "instance";
    private static final String LIST_LINK = "parent";
    private static final String UPDATED_BY_LINK = "updatedBy";

    private final String region;

    private final String accessKey;

    private final DateTime updated;

    private final String updatedBy;

    private final String secretKey;

    private final Map<String, String> links;

    /**
     * Used to add new S3 credentials
     *
     * @param region    S3 region
     * @param accessKey S3 access key
     * @param secretKey S3 secret key
     */
    public WarehouseS3Credentials(final String region,
                                  final String accessKey,
                                  final String secretKey) {
        this(region, accessKey, secretKey, null, null, null);
    }

    /**
     * Used to list saved S3 credentials (not intended for the end user)
     *
     * @param region    S3 region
     * @param accessKey S3 access key
     * @param updatedBy URI of the user who updated the credentials last
     * @param updated   last modification date
     */
    public WarehouseS3Credentials(final String region,
                                  final String accessKey,
                                  final String updatedBy,
                                  final DateTime updated) {
        this(region, accessKey, null, updatedBy, updated, null);
    }

    @JsonCreator
    WarehouseS3Credentials(@JsonProperty("region") final String region,
                           @JsonProperty("accessKey") final String accessKey,
                           @JsonProperty("secretKey") final String secretKey,
                           @JsonProperty("updatedBy") final String updatedBy,
                           @JsonProperty("updated") @JsonDeserialize(using = ISODateTimeDeserializer.class) final DateTime updated,
                           @JsonProperty("links") final Map<String, String> links) {
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.updatedBy = updatedBy;
        this.updated = updated;
        this.links = links;
    }

    @JsonSerialize(using = ISODateTimeSerializer.class)
    public DateTime getUpdated() {
        return updated;
    }

    /**
     * @return URI of the user who updated the credentials last
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * not returned when listing
     * @return S3 secret key
     */
    public String getSecretKey() {
        return secretKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getRegion() {
        return region;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    /**
     * Adds secret key
     *
     * @param secretKey secret key to add
     * @return S3 credentials with secret key
     */
    public WarehouseS3Credentials withSecretKey(final String secretKey) {
        return new WarehouseS3Credentials(region, accessKey, secretKey, updatedBy, updated, links);
    }

    /**
     * Adds links
     *
     * @param links links to add
     * @return S3 credentials with links
     */
    WarehouseS3Credentials withLinks(final Map<String, String> links) {
        return new WarehouseS3Credentials(region, accessKey, secretKey, updatedBy, updated, links);
    }

    /**
     * Adds links using provided warehouse id
     *
     * @param warehouseId warehouse id to use in links
     * @return S3 credentials with links based on specified warehouse
     */
    public WarehouseS3Credentials withLinks(final String warehouseId) {
        final Map<String, String> links = new HashMap<>();
        links.put(INSTANCE_LINK, Warehouse.TEMPLATE.expand(warehouseId).toString());
        links.put(LIST_LINK, WarehouseS3CredentialsList.TEMPLATE.expand(warehouseId).toString());
        links.put(SELF_LINK, TEMPLATE.expand(warehouseId, region, accessKey).toString());

        if (updatedBy != null) {
            links.put(UPDATED_BY_LINK, Account.TEMPLATE.expand(updatedBy).toString());
        }
        return withLinks(links);
    }

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").get(SELF_LINK);
    }

    @JsonIgnore
    public String getListUri() {
        return notNullState(links, "links").get(LIST_LINK);
    }

    @JsonIgnore
    public String getInstanceUri() {
        return notNullState(links, "links").get(INSTANCE_LINK);
    }

    @JsonIgnore
    public String getUpdatedByUri() {
        return notNullState(links, "links").get(UPDATED_BY_LINK);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this, "secretKey");
    }
}
