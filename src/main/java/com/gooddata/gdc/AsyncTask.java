/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Asynchronous task containing link for polling.
 * Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("asyncTask")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AsyncTask {

    @JsonProperty
    private final Link link;

    @JsonCreator
    private AsyncTask(@JsonProperty("link") Link link) {
        this.link = link;
    }

    public AsyncTask(final String uri) {
        this.link = new Link(uri);
    }

    @JsonIgnore
    public String getUri() {
        return link.getPoll();
    }

    private static class Link {

        private final String poll;

        @JsonCreator
        private Link(@JsonProperty("poll") String poll) {
            this.poll = poll;
        }

        public String getPoll() {
            return poll;
        }
    }

}
