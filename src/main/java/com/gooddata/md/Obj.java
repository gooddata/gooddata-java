/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.web.util.UriTemplate;

/**
 * Metadata object
 */
public abstract class Obj {
    public static final String URI = "/gdc/md/{projectId}/obj";
    public static final String OBJ_URI = URI + "/{objId}";
    public static final UriTemplate OBJ_TEMPLATE = new UriTemplate(OBJ_URI);

    protected final Meta meta;

    public Obj(@JsonProperty("meta") Meta meta) {
        this.meta = meta;
    }

    public Meta getMeta() {
        return meta;
    }
}
