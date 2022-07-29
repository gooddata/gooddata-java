/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.gdc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.List;

/**
 * Collection of links. Typically used as a result of asynchronous task returning more links.
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkEntries {

    private final List<LinkEntry> entries;

    @JsonCreator
    protected LinkEntries(@JsonProperty("entries") List<LinkEntry> entries) {
        this.entries = entries;
    }

    protected List<LinkEntry> getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class LinkEntry {
        private final String uri;
        private final String category;

        @JsonCreator
        private LinkEntry(@JsonProperty("link") String uri, @JsonProperty("category") String category) {
            this.uri = uri;
            this.category = category;
        }

        public String getUri() {
            return uri;
        }

        public String getCategory() {
            return category;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
