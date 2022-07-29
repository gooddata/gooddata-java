/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.gdc;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
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

        public Link(String identifier, String uri, String title) {
            this(identifier, uri, title, null, null);
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

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
