/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Asynchronous task containing link for polling.
 * Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("asyncTask")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AsyncTask {

    private Link link;

    @JsonCreator
    private AsyncTask(@JsonProperty("link") Link link) {
        this.link = link;
    }

    public String getUri() {
        return link.pollUri;
    }

    private static class Link {
        private String pollUri;

        @JsonCreator
        private Link(@JsonProperty("poll") String pollUri) {
            this.pollUri = pollUri;
        }
    }

}
