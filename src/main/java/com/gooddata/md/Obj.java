/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.web.util.UriTemplate;

/**
 * Metadata object (common part)
 */
public abstract class Obj {
    public static final String URI = "/gdc/md/{projectId}/obj";
    public static final String OBJ_URI = URI + "/{objId}";
    public static final UriTemplate OBJ_TEMPLATE = new UriTemplate(OBJ_URI);

    @JsonProperty("meta")
    protected final Meta meta;

    protected Obj(@JsonProperty("meta") Meta meta) {
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
    public String getProjectTemplate() {
        return meta.getProjectTemplate();
    }

    @JsonIgnore
    public Integer getLocked() {
        return meta.getLocked();
    }

    @JsonIgnore
    public Integer getUnlisted() {
        return meta.getUnlisted();
    }

}
