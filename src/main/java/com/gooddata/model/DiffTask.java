/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * TODO may be move somewhere to common
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("asyncTask")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DiffTask {

    private Link link;

    @JsonCreator
    public DiffTask(@JsonProperty("link") Link link) {
        this.link = link;
    }

    public String getUri() {
        return link.pollUri;
    }

    public static class Link {
        private String pollUri;

        @JsonCreator
        public Link(@JsonProperty("poll") String pollUri) {
            this.pollUri = pollUri;
        }
    }

}
