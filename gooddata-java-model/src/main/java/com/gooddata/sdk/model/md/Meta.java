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
import com.gooddata.util.BooleanDeserializer;
import com.gooddata.util.BooleanIntegerSerializer;
import com.gooddata.util.BooleanStringSerializer;
import com.gooddata.util.GDZonedDateTime;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.substring;

/**
 * Metadata meta information (meant just for internal SDK usage)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta implements Serializable {

    private static final long serialVersionUID = -8102041850487115166L;
    private static final int SUMMARY_MAX_LENGTH = 2048;
    private static final int TITLE_MAX_LENGTH = 255;

    private String author;
    private String contributor;
    @GDZonedDateTime
    private ZonedDateTime created;
    @GDZonedDateTime
    private ZonedDateTime updated;
    private String summary;
    private String title;
    private String category;
    private Set<String> tags;
    private String uri;
    private String identifier;
    private Boolean deprecated;
    private Boolean production;
    private Boolean locked;
    private Boolean unlisted;
    private Boolean sharedWithSomeone;
    private Set<String> flags;

    @JsonCreator
    protected Meta(@JsonProperty("author") String author,
                   @JsonProperty("contributor") String contributor,
                   @JsonProperty("created") ZonedDateTime created,
                   @JsonProperty("updated") ZonedDateTime updated,
                   @JsonProperty("summary") String summary,
                   @JsonProperty("title") String title,
                   @JsonProperty("category") String category,
                   @JsonProperty("tags") @JsonDeserialize(using = TagsDeserializer.class) Set<String> tags,
                   @JsonProperty("uri") String uri,
                   @JsonProperty("identifier") String identifier,
                   @JsonProperty("flags") Set<String> flags) {
        super();
        this.author = author;
        this.uri = uri;
        this.tags = tags;
        this.created = created;
        this.summary = substring(summary, 0, SUMMARY_MAX_LENGTH);
        this.title = substring(title, 0, TITLE_MAX_LENGTH);
        this.updated = updated;
        this.category = category;
        this.identifier = identifier;
        this.contributor = contributor;
        this.flags = flags;
    }

    /**
     * Constructor with "extra" flags argument
     *
     * @param author
     * @param contributor
     * @param created
     * @param updated
     * @param summary
     * @param title
     * @param category
     * @param tags
     * @param uri
     * @param identifier
     * @param deprecated
     * @param production
     * @param locked
     * @param unlisted
     * @param sharedWithSomeone
     * @param flags
     */
    public Meta(String author, String contributor, ZonedDateTime created, ZonedDateTime updated, String summary,
                String title, String category, Set<String> tags, String uri, String identifier,
                Boolean deprecated, Boolean production, Boolean locked, Boolean unlisted,
                Boolean sharedWithSomeone, Set<String> flags) {
        this.author = author;
        this.contributor = contributor;
        this.created = created;
        this.updated = updated;
        this.summary = summary;
        this.title = title;
        this.category = category;
        this.tags = tags;
        this.uri = uri;
        this.identifier = identifier;
        this.deprecated = deprecated;
        this.production = production;
        this.locked = locked;
        this.unlisted = unlisted;
        this.sharedWithSomeone = sharedWithSomeone;
        this.flags = flags;
    }

    public Meta(String title) {
        this(title, null);
    }

    public Meta(String title, String summary) {
        this.title = title;
        this.summary = summary;
    }

    /**
     * Returns internally generated ID of the object (that's part of the object URI).
     *
     * @return internal ID of the object
     */
    @JsonIgnore
    public String getId() {
        return UriHelper.getLastUriPart(getUri());
    }

    public String getAuthor() {
        return author;
    }

    public String getContributor() {
        return contributor;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @JsonSerialize(using = TagsSerializer.class)
    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getUri() {
        return uri;
    }

    /**
     * Default is false/not-deprecated.
     *
     * @return true when the linked object is deprecated, null if not set
     */
    @JsonProperty("deprecated")
    @JsonSerialize(using = BooleanStringSerializer.class)
    public Boolean isDeprecated() {
        return deprecated;
    }

    @JsonProperty("deprecated")
    @JsonDeserialize(using = BooleanDeserializer.class)
    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    /**
     * Returns user-specified identifier of the object.
     *
     * @return user-specified object identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Is the object production or not? Defaults to true.
     *
     * @return true when the linked object is production, null if not set
     */
    @JsonProperty("isProduction")
    @JsonSerialize(using = BooleanIntegerSerializer.class)
    public Boolean isProduction() {
        return production;
    }

    @JsonProperty("isProduction")
    @JsonDeserialize(using = BooleanDeserializer.class)
    public void setProduction(final Boolean production) {
        this.production = production;
    }

    /**
     * Flag that MD object is locked; default is false/unlocked.
     *
     * @return true when the linked object is locked, null if not set
     */
    @JsonProperty("locked")
    @JsonSerialize(using = BooleanIntegerSerializer.class)
    public Boolean isLocked() {
        return locked;
    }

    @JsonProperty("locked")
    @JsonDeserialize(using = BooleanDeserializer.class)
    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    /**
     * Default is false/listed.
     *
     * @return true when the linked object is unlisted, null if not set
     */
    @JsonProperty("unlisted")
    @JsonSerialize(using = BooleanIntegerSerializer.class)
    public Boolean isUnlisted() {
        return unlisted;
    }

    @JsonProperty("unlisted")
    @JsonDeserialize(using = BooleanDeserializer.class)
    public void setUnlisted(Boolean unlisted) {
        this.unlisted = unlisted;
    }

    /**
     * Is the linked object shared with someone via ACLs?
     *
     * @return true when the linked object is shared, null if not set
     */
    @JsonProperty("sharedWithSomeone")
    @JsonSerialize(using = BooleanIntegerSerializer.class)
    public Boolean isSharedWithSomeone() {
        return sharedWithSomeone;
    }

    @JsonProperty("sharedWithSomeone")
    @JsonDeserialize(using = BooleanDeserializer.class)
    public void setSharedWithSomeone(final Boolean sharedWithSomeone) {
        this.sharedWithSomeone = sharedWithSomeone;
    }

    public Set<String> getFlags() {
        return flags;
    }

    public void setFlags(final Set<String> flags) {
        this.flags = flags;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
