/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Set;

import static com.gooddata.util.Validate.noNullElements;

/**
 * Metadata object (common part)
 */
public abstract class AbstractObj implements Serializable {

    private static final long serialVersionUID = 2910760851810495274L;

    @JsonProperty("meta")
    protected final Meta meta;

    protected AbstractObj(@JsonProperty("meta") Meta meta) {
        this.meta = meta;
    }

    /**
     * Returns internally generated ID of the object (that's part of the object URI).
     *
     * @return internal ID of the object
     */
    @JsonIgnore
    public String getId() {
        return Obj.OBJ_TEMPLATE.match(getUri()).get("objId");
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

    /**
     * Returns user-specified identifier of the object.
     *
     * @return user-specified object identifier
     */
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

    @JsonIgnore
    public Set<String> getFlags() {
        return meta.getFlags();
    }

    public void setFlags(final Set<String> flags) {
        meta.setFlags(flags);
    }

    /**
     * Get list of URIs of the given {@link Obj}s
     *
     * @param objs metadata objects
     * @param <T>  Obj type
     * @return list of URIs
     */
    @SafeVarargs
    protected static <T extends Obj> String[] uris(T... objs) {
        noNullElements(objs, "objs");
        final String[] uris = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            uris[i] = objs[i].getUri();
        }
        return uris;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
