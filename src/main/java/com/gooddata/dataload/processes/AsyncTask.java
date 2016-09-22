/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload.processes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Asynchronous task containing link for polling.
 * This task differs from {@link com.gooddata.gdc.AsyncTask} in links field.
 * Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("asyncTask")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class AsyncTask {

    @JsonProperty
    private Links links;

    @JsonCreator
    private AsyncTask(@JsonProperty("links") Links links) {
        this.links = links;
    }

    public AsyncTask(final String uri) {
        this.links = new Links(uri);
    }

    @JsonIgnore
    public String getUri() {
        return links.getPoll();
    }

    private static class Links {

        private final String poll;

        @JsonCreator
        private Links(@JsonProperty("poll") String poll) {
            this.poll = poll;
        }

        public String getPoll() {
            return poll;
        }
    }

}
