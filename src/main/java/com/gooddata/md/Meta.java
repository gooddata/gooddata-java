/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.gooddata.util.*;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Metadata meta information (meant just for internal SDK usage)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = Inclusion.NON_NULL)
public class Meta implements Serializable {

    private String author;
    private String contributor;
    private DateTime created;
    private DateTime updated;
    private String summary;
    private String category;
    private String tags; //TODO collection
    private String uri;
    private boolean deprecated;
    private String title;
    private String identifier;
    private boolean locked;
    private boolean unlisted;

    @JsonCreator
    protected Meta(@JsonProperty("author") String author,
                   @JsonProperty("contributor") String contributor,
                   @JsonProperty("created") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime created,
                   @JsonProperty("updated") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime updated,
                   @JsonProperty("summary") String summary,
                   @JsonProperty("title") String title,
                   @JsonProperty("category") String category,
                   @JsonProperty("tags") String tags,
                   @JsonProperty("uri") String uri,
                   @JsonProperty("deprecated") @JsonDeserialize(using = BooleanStringDeserializer.class) boolean deprecated,
                   @JsonProperty("identifier") String identifier,
                   @JsonProperty("locked") @JsonDeserialize(using = BooleanIntegerDeserializer.class) boolean locked,
                   @JsonProperty("unlisted") @JsonDeserialize(using = BooleanIntegerDeserializer.class) boolean unlisted) {
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
        this.contributor = contributor;
        this.locked = locked;
        this.unlisted = unlisted;
    }

    public Meta(String title) {
        this.title = title;
    }

    public Meta(String title, String summary) {
        this.title = title;
        this.summary = summary;
    }

    public String getAuthor() {
        return author;
    }

    public String getContributor() {
        return contributor;
    }

    @JsonSerialize(using = GDDateTimeSerializer.class, include = Inclusion.NON_NULL)
    public DateTime getCreated() {
        return created;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    @JsonSerialize(using = GDDateTimeSerializer.class, include = Inclusion.NON_NULL)
    public DateTime getUpdated() {
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

    @JsonSerialize(using = BooleanStringSerializer.class)
    public boolean isDeprecated() {
        return deprecated;
    }

    public String getIdentifier() {
        return identifier;
    }

	@JsonSerialize(using = BooleanIntegerSerializer.class)
    public boolean isLocked() {
        return locked;
    }

	@JsonSerialize(using = BooleanIntegerSerializer.class)
    public boolean isUnlisted() {
        return unlisted;
    }

}
