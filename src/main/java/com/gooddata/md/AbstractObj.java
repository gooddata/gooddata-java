/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.util.Set;

import static com.gooddata.util.Validate.noNullElements;

/**
 * Metadata object (common part)
 */
public abstract class AbstractObj {

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
    public Set<String> getTags() {
        return meta.getTags();
    }

    public void setTags(Set<String> tags) {
        meta.setTags(tags);
    }

    @JsonIgnore
    public String getUri() {
        return meta.getUri();
    }

    @JsonIgnore
    public boolean isDeprecated() {
        return Boolean.TRUE.equals(meta.isDeprecated());
    }

    public void setDeprecated(Boolean deprecated) {
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
        return Boolean.TRUE.equals(meta.isLocked());
    }

    public void setLocked(Boolean locked) {
        meta.setLocked(locked);
    }

    @JsonIgnore
    public boolean isUnlisted() {
        return Boolean.TRUE.equals(meta.isUnlisted());
    }

    public void setUnlisted(Boolean unlisted) {
        meta.setUnlisted(unlisted);
    }

    @JsonIgnore
    public boolean isProduction() {
        return Boolean.TRUE.equals(meta.isProduction());
    }

    public void setProduction(Boolean production) {
        meta.setProduction(production);
    }

    @JsonIgnore
    public boolean isSharedWithSomeone() {
        return Boolean.TRUE.equals(meta.isSharedWithSomeone());
    }

    public void setSharedWithSomeone(Boolean sharedWithSomeone) {
        meta.setSharedWithSomeone(sharedWithSomeone);
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
