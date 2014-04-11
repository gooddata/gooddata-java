/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Metadata object
 */
public abstract class Obj {
    public static final String URI = "/gdc/md/{projectId}/obj";
    public static final String OBJ_URI = URI + "/{objId}";

    protected final Meta meta;

    public Obj(@JsonProperty("meta") Meta meta) {
        this.meta = meta;
    }

    public Meta getMeta() {
        return meta;
    }
}
