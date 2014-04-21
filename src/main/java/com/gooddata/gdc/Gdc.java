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

import java.util.Collection;
import java.util.List;

/**
 */
@JsonTypeName("about")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Gdc {

    public static final String URI = "/gdc";

    private final String category;
    private final String summary;
    private final List<Link> links;

    @JsonCreator
    public Gdc(@JsonProperty("category") String category, @JsonProperty("summary") String summary,
               @JsonProperty("links") List<Link> links) {
        this.category = category;
        this.summary = summary;
        this.links = links;
    }

    public String getCategory() {
        return category;
    }

    public String getSummary() {
        return summary;
    }

    public Collection<Link> getLinks() {
        return links;
    }

    public Link getLink(String category) {
        for (Link link: links) {
            if (category.equals(link.getCategory())) {
                return link;
            }
        }
        return null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Link {
        private final String category;
        private final String link;
        private final String summary;
        private final String title;

        @JsonCreator
        public Link(@JsonProperty("category") String category, @JsonProperty("link") String link,
                    @JsonProperty("summary") String summary, @JsonProperty("title") String title) {
            this.category = category;
            this.link = link;
            this.summary = summary;
            this.title = title;
        }

        public String getCategory() {
            return category;
        }

        public String getLink() {
            return link;
        }

        public String getSummary() {
            return summary;
        }

        public String getTitle() {
            return title;
        }
    }
}
