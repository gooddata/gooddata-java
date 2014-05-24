/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import java.io.Serializable;

/**
 * Metadata meta information (meant just for internal SDK usage)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = Inclusion.NON_NULL)
public class Meta implements Serializable {

    private String author;
    private String contributor;
    private String created; //TODO date time
    private String updated; //TODO date time
    private String summary;
    private String category;
    private String tags; //TODO collection
    private String uri;
    private String deprecated; //TODO boolean
    private String title;
    private String identifier;
    private String projectTemplate;
    private Integer locked; //TODO boolean
    private Integer unlisted; //TODO boolean

    @JsonCreator
    protected Meta(@JsonProperty("author") String author,
                   @JsonProperty("contributor") String contributor,
                   @JsonProperty("created") String created,
                   @JsonProperty("updated") String updated,
                   @JsonProperty("summary") String summary,
                   @JsonProperty("title") String title,
                   @JsonProperty("category") String category,
                   @JsonProperty("tags") String tags,
                   @JsonProperty("uri") String uri,
                   @JsonProperty("deprecated") String deprecated,
                   @JsonProperty("identifier") String identifier,
                   @JsonProperty("projectTemplate") String projectTemplate,
                   @JsonProperty("locked") Integer locked,
                   @JsonProperty("unlisted") Integer unlisted) {
        super();
        this.author = author;
        this.uri = uri;
        this.tags = tags;
        this.created = created;
        this.summary = summary;
        this.title = title;
        this.updated = updated;
        this.category = category;
        this.deprecated = deprecated;
        this.identifier = identifier;
        this.projectTemplate = projectTemplate;
        this.contributor = contributor;
        this.locked = locked;
        this.unlisted = unlisted;
    }

    public Meta(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContributor() {
        return contributor;
    }

    public String getCreated() {
        return created;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    public String getUpdated() {
        return updated;
    }

    public String getCategory() {
        return category;
    }

    public String getTags() {
        return tags;
    }

    public String getUri() {
        return uri;
    }

    public String getDeprecated() {
        return deprecated;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getProjectTemplate() {
        return projectTemplate;
    }

    public Integer getLocked() {
        return locked;
    }

    public Integer getUnlisted() {
        return unlisted;
    }
}
