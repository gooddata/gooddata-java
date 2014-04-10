/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 */
public class UriResponse {

    private final String uri;

    @JsonCreator
    public UriResponse(@JsonProperty("uri") String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
