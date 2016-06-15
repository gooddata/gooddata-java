/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.util.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.substring;

/**
 * Metadata meta information (meant just for internal SDK usage)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta implements Serializable {

    private static final int SUMMARY_MAX_LENGTH = 2048;
    private static final int TITLE_MAX_LENGTH = 255;

    private String author;
    private String contributor;
    private DateTime created;
    private DateTime updated;
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

    @JsonCreator
    protected Meta(@JsonProperty("author") String author,
                   @JsonProperty("contributor") String contributor,
                   @JsonProperty("created") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime created,
                   @JsonProperty("updated") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime updated,
                   @JsonProperty("summary") String summary,
                   @JsonProperty("title") String title,
                   @JsonProperty("category") String category,
                   @JsonProperty("tags") @JsonDeserialize(using = TagsDeserializer.class) Set<String> tags,
                   @JsonProperty("uri") String uri,
                   @JsonProperty("identifier") String identifier) {
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
    }

    public Meta(String author, String contributor, DateTime created, DateTime updated, String summary,
            String title, String category, Set<String> tags, String uri, String identifier,
            Boolean deprecated, Boolean production, Boolean locked, Boolean unlisted, Boolean sharedWithSomeone) {
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
    }

    public Meta(String title) {
        this(title, null);
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

    @JsonSerialize(using = GDDateTimeSerializer.class)
    public DateTime getCreated() {
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

    @JsonSerialize(using = GDDateTimeSerializer.class)
    public DateTime getUpdated() {
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Is the object production or not? Defaults to true.
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
}
