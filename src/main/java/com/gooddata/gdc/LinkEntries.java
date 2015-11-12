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
        private final String link;
        private final String category;

        @JsonCreator
        private LinkEntry(@JsonProperty("link") String link, @JsonProperty("category") String category) {
            this.link = link;
            this.category = category;
        }

        public String getLink() {
            return link;
        }

        public String getCategory() {
            return category;
        }
    }
}
