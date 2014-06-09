/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import com.gooddata.gdc.GdcError;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;

/**
 * MAQL DDL task status
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("wTaskStatus")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class MaqlDdlTaskStatus {

    private static final String OK = "OK";

    private final String status;

    private final String uri;

    private final Collection<GdcError> messages;

    @JsonCreator
    public MaqlDdlTaskStatus(@JsonProperty("status") String status, @JsonProperty("poll") String uri,
                             @JsonProperty("messages") Collection<GdcError> messages) {
        this.status = status;
        this.uri = uri;
        this.messages = messages;
    }

    public String getStatus() {
        return status;
    }

    public String getUri() {
        return uri;
    }

    public Collection<GdcError> getMessages() {
        return messages;
    }

    public boolean isSuccess() {
        return OK.equals(status);
    }
}
