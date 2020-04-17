/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.sdk.model.util.TagsDeserializer;
import com.gooddata.sdk.model.util.TagsSerializer;
import com.gooddata.sdk.model.util.UriHelper;
import com.gooddata.sdk.common.util.BooleanDeserializer;
import com.gooddata.sdk.common.util.BooleanIntegerSerializer;
import com.gooddata.sdk.common.util.BooleanStringSerializer;
import com.gooddata.sdk.common.util.GDZonedDateTime;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.time.ZonedDateTime;
import java.util.Set;

/**
 * Metadata entry (can be named "LINK" in some API docs)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Entry {

    private final String uri;
    private final String title;
    private final String summary;
    private final String category;
    private final String author;
    private final String contributor;
    private final Boolean deprecated;
    private final String identifier;
    private final Set<String> tags;
    @GDZonedDateTime
    private final ZonedDateTime created;
    @GDZonedDateTime
    private final ZonedDateTime updated;
    private final Boolean locked;
    private final Boolean unlisted;

    @JsonCreator
    public Entry(@JsonProperty("link") String uri,
                 @JsonProperty("title") String title,
                 @JsonProperty("summary") String summary,
                 @JsonProperty("category") String category,
                 @JsonProperty("author") String author,
                 @JsonProperty("contributor") String contributor,
                 @JsonProperty("deprecated") @JsonDeserialize(using = BooleanDeserializer.class) Boolean deprecated,
                 @JsonProperty("identifier") String identifier,
                 @JsonProperty("tags") @JsonDeserialize(using = TagsDeserializer.class) Set<String> tags,
                 @JsonProperty("created") ZonedDateTime created,
                 @JsonProperty("updated") ZonedDateTime updated,
                 @JsonProperty("locked") @JsonDeserialize(using = BooleanDeserializer.class) Boolean locked,
                 @JsonProperty("unlisted") @JsonDeserialize(using = BooleanDeserializer.class) Boolean unlisted) {
        this.uri = uri;
        this.title = title;
        this.summary = summary;
        this.category = category;
        this.author = author;
        this.contributor = contributor;
        this.deprecated = deprecated;
        this.identifier = identifier;
        this.tags = tags;
        this.created = created;
        this.updated = updated;
        this.locked = locked;
        this.unlisted = unlisted;
    }

    /**
     * Returns internally generated ID of the object (that's part of the object URI).
     * @return internal ID of the object
     */
    @JsonIgnore
    public String getId() {
        return UriHelper.getLastUriPart(getUri());
    }

    @JsonProperty("link")
    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getContributor() {
        return contributor;
    }

    /**
     * @return true if the deprecated is set and is true
     */
    @JsonIgnore
    public boolean isDeprecated() {
        return Boolean.TRUE.equals(deprecated);
    }

    @JsonSerialize(using = BooleanStringSerializer.class)
    public Boolean getDeprecated() {
        return deprecated;
    }

    /**
     * Returns user-specified identifier of the object.
     * @return user-specified object identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    @JsonSerialize(using = TagsSerializer.class)
    public Set<String> getTags() {
        return tags;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    /**
     * @return true if the locked is set and is true
     */
    @JsonIgnore
    public boolean isLocked() {
        return Boolean.TRUE.equals(locked);
    }

    @JsonSerialize(using = BooleanIntegerSerializer.class)
    public Boolean getLocked() {
        return locked;
    }

    /**
     * @return true if the unlisted is set and is true
     */
    @JsonIgnore
    public boolean isUnlisted() {
        return Boolean.TRUE.equals(unlisted);
    }

    @JsonSerialize(using = BooleanIntegerSerializer.class)
    public Boolean getUnlisted() {
        return unlisted;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
