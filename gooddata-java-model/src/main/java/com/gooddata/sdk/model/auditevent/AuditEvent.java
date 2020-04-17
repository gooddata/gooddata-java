/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.auditevent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import com.gooddata.sdk.common.util.ISOZonedDateTime;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Audit event
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(AuditEvent.ROOT_NODE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditEvent {

    public static final String GDC_URI = "/gdc";
    public static final String USER_URI = GDC_URI + "/account/profile/{userId}/auditEvents";
    public static final String ADMIN_URI = GDC_URI + "/domains/{domainId}/auditEvents";

    static final String ROOT_NODE = "event";

    private final String id;

    private final String userLogin;

    /** the time the event occurred */
    @ISOZonedDateTime
    private final ZonedDateTime occurred;

    /** the time event was recorded by audit system */
    @ISOZonedDateTime
    private final ZonedDateTime recorded;

    private final String userIp;

    private final boolean success;

    private final String type;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Map<String, String> params;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Map<String, String> links;

    @JsonCreator
    public AuditEvent(@JsonProperty("id") String id,
                      @JsonProperty("userLogin") String userLogin,
                      @JsonProperty("occurred") ZonedDateTime occurred,
                      @JsonProperty("recorded") ZonedDateTime recorded,
                      @JsonProperty("userIp") String userIp,
                      @JsonProperty("success") boolean success,
                      @JsonProperty("type") String type,
                      @JsonProperty("params") Map<String, String> params,
                      @JsonProperty("links") Map<String, String> links) {
        this.id = id;
        this.userLogin = userLogin;
        this.occurred = occurred;
        this.recorded = recorded;
        this.userIp = userIp;
        this.success = success;
        this.type = type;
        this.params = params;
        this.links = links;
    }

    public String getId() {
        return id;
    }

    public String getUserLogin() {
        return userLogin;
    }

    /**
     * the time the event occurred
     */
    public ZonedDateTime getOccurred() {
        return occurred;
    }

    /**
     * the time event was recorded by audit system
     */
    public ZonedDateTime getRecorded() {
        return recorded;
    }

    public String getUserIp() {
        return userIp;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
