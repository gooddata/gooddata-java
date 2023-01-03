/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import com.fasterxml.jackson.annotation.*;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Asynchronous ETL Pull 2 task (for internal use).
 * Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("pull2Task")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullTask {

    public static final String URI = "/gdc/md/{projectId}/tasks/task/{taskId}";

    private final Links links;

    @JsonCreator
    private PullTask(@JsonProperty("links") Links links) {
        notNull(links, "links");

        this.links = links;
    }

    public String getPollUri() {
        return links.uri;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Links {

        private final String uri;

        @JsonCreator
        private Links(@JsonProperty("poll") String uri) {
            notEmpty(uri, "uri");

            this.uri = uri;
        }
    }
}
