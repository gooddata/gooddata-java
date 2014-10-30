/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.gooddata.Validate.noNullElements;

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
    public String getCreated() {
        return meta.getCreated();
    }

    @JsonIgnore
    public String getSummary() {
        return meta.getSummary();
    }

    @JsonIgnore
    public String getTitle() {
        return meta.getTitle();
    }

    @JsonIgnore
    public String getUpdated() {
        return meta.getUpdated();
    }

    @JsonIgnore
    public String getCategory() {
        return meta.getCategory();
    }

    @JsonIgnore
    public String getTags() {
        return meta.getTags();
    }

    @JsonIgnore
    public String getUri() {
        return meta.getUri();
    }

    @JsonIgnore
    public String getDeprecated() {
        return meta.getDeprecated();
    }

    @JsonIgnore
    public String getIdentifier() {
        return meta.getIdentifier();
    }

    @JsonIgnore
    public Integer getLocked() {
        return meta.getLocked();
    }

    @JsonIgnore
    public Integer getUnlisted() {
        return meta.getUnlisted();
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
