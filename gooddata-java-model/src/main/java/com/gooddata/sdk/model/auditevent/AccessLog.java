/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.auditevent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import com.gooddata.sdk.common.util.ISOZonedDateTime;

import java.time.ZonedDateTime;

/**
 * Model class, used for special audit log events/access logs directly from haproxy,
 * that represents access logs for particular hosts.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("accessLog")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessLog {

    public static final String RESOURCE_URI = "/gdc/domains/{domainId}/accessLogs";

    private final String id;
    private final String host;
    private final String path;
    private final String method;
    private final String code;
    private final String size;
    private final String userIp;
    @ISOZonedDateTime
    private final ZonedDateTime occurred;
    @ISOZonedDateTime
    private final ZonedDateTime recorded;

    @JsonCreator
    public AccessLog(@JsonProperty("id") String id, @JsonProperty("host") String host, @JsonProperty("path") String path,
                     @JsonProperty("method") String method, @JsonProperty("code") String code, @JsonProperty("size") String size,
                     @JsonProperty("userIp") String userIp, @JsonProperty("occurred") ZonedDateTime occurred, @JsonProperty("recorded") ZonedDateTime recorded) {
        this.id = id;
        this.host = host;
        this.path = path;
        this.method = method;
        this.code = code;
        this.size = size;
        this.userIp = userIp;
        this.occurred = occurred;
        this.recorded = recorded;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public String getCode() {
        return code;
    }

    public String getSize() {
        return size;
    }

    public String getUserIp() {
        return userIp;
    }

    public ZonedDateTime getOccurred() {
        return occurred;
    }

    public ZonedDateTime getRecorded() {
        return recorded;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
