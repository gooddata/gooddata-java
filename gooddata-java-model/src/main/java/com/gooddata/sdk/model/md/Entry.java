/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.model.util.TagsDeserializer;
import com.gooddata.sdk.model.util.TagsSerializer;
import com.gooddata.util.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

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
    private final DateTime created;
    private final DateTime updated;
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
                 @JsonProperty("created") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime created,
                 @JsonProperty("updated") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime updated,
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
     * @return self URI string
     * @deprecated use {@link #getUri()} instead
     */
    @Deprecated
    public String getLink() {
        return getUri();
    }

    /**
     * Returns internally generated ID of the object (that's part of the object URI).
     * @return internal ID of the object
     */
    @JsonIgnore
    public String getId() {
        return Obj.OBJ_TEMPLATE.match(getUri()).get("objId");
    }

    @JsonIgnore
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

    @JsonSerialize(using = GDDateTimeSerializer.class)
    public DateTime getCreated() {
        return created;
    }

    @JsonSerialize(using = GDDateTimeSerializer.class)
    public DateTime getUpdated() {
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
