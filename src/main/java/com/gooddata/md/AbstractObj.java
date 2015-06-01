/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import static com.gooddata.util.Validate.noNullElements;

/**
 * Metadata object (common part)
 */
public abstract class AbstractObj implements Obj {

    @JsonProperty("meta")
    protected final Meta meta;

    protected AbstractObj(@JsonProperty("meta") Meta meta) {
        this.meta = meta;
    }

    @JsonIgnore
    public String getAuthor() {
        return meta.getAuthor();
    }

    @JsonIgnore
    public String getContributor() {
        return meta.getContributor();
    }

    @JsonIgnore
    public DateTime getCreated() {
        return meta.getCreated();
    }

    @JsonIgnore
    public String getSummary() {
        return meta.getSummary();
    }

    public void setSummary(String summary) {
        meta.setSummary(summary);
    }

    @JsonIgnore
    public String getTitle() {
        return meta.getTitle();
    }

    public void setTitle(String title) {
        meta.setTitle(title);
    }

    @JsonIgnore
    public DateTime getUpdated() {
        return meta.getUpdated();
    }

    @JsonIgnore
    public String getCategory() {
        return meta.getCategory();
    }

    public void setCategory(String category) {
        meta.setCategory(category);
    }

    @JsonIgnore
    public String getTags() {
        return meta.getTags();
    }

    public void setTags(String tags) {
        meta.setTags(tags);
    }

    @JsonIgnore
    public String getUri() {
        return meta.getUri();
    }

    @JsonIgnore
    public boolean isDeprecated() {
        return meta.isDeprecated();
    }

    public void setDeprecated(boolean deprecated) {
        meta.setDeprecated(deprecated);
    }

    @JsonIgnore
    public String getIdentifier() {
        return meta.getIdentifier();
    }

    public void setIdentifier(String identifier) {
        meta.setIdentifier(identifier);
    }

    @JsonIgnore
    public boolean isLocked() {
        return meta.isLocked();
    }

    public void setLocked(boolean locked) {
        meta.setLocked(locked);
    }

    @JsonIgnore
    public boolean isUnlisted() {
        return meta.isUnlisted();
    }

    public void setUnlisted(boolean unlisted) {
        meta.setUnlisted(unlisted);
    }

    /**
     * Get list of URIs of the given {@link Obj}s
     * @param objs metadata objects
     * @param <T> Obj type
     * @return list of URIs
     */
    @SafeVarargs
    protected static <T extends Obj> String[] uris(T... objs) {
        noNullElements(objs, "objs");
        final String[] uris = new String[objs.length];
        for (int i=0; i<objs.length; i++) {
            uris[i] = objs[i].getUri();
        }
        return uris;
    }

}
