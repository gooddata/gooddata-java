/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class LinkEntry {
        private final String uri;
        private final String category;

        @JsonCreator
        private LinkEntry(@JsonProperty("link") String uri, @JsonProperty("category") String category) {
            this.uri = uri;
            this.category = category;
        }

        /**
         * @return self URI string
         * @deprecated use {@link #getUri()} instead
         */
        @Deprecated
        public String getLink() {
            return getUri();
        }

        public String getUri() {
            return uri;
        }

        public String getCategory() {
            return category;
        }
    }
}
