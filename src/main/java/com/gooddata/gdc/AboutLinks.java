/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * Collection of links with "about" metadata.
 * Deserialization only.
 */
@JsonTypeName("about")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AboutLinks {

    private final String category;
    private final String summary;
    private final String instance;
    private final List<Link> links;

    @JsonCreator
    public AboutLinks(@JsonProperty("category") String category, @JsonProperty("summary") String summary,
                      @JsonProperty("instance") String instance, @JsonProperty("links") List<Link> links) {
        this.category = category;
        this.summary = summary;
        this.instance = instance;
        this.links = links;
    }

    public String getCategory() {
        return category;
    }

    public String getSummary() {
        return summary;
    }

    public String getInstance() {
        return instance;
    }

    public Collection<Link> getLinks() {
        return links;
    }

    /**
     * Link with metadata.
     * Deserialization only.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Link {
        private final String identifier;
        private final String uri;
        private final String title;
        private final String category;
        private final String summary;

        @JsonCreator
        public Link(@JsonProperty("identifier") String identifier, @JsonProperty("link") String uri,
                    @JsonProperty("title") String title, @JsonProperty("category") String category,
                    @JsonProperty("summary") String summary) {
            this.identifier = identifier;
            this.uri = uri;
            this.title = title;
            this.category = category;
            this.summary = summary;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getUri() {
            return uri;
        }

        public String getTitle() {
            return title;
        }

        public String getCategory() {
            return category;
        }

        public String getSummary() {
            return summary;
        }
    }
}
