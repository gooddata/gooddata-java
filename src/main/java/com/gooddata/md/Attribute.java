/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import static java.util.Arrays.asList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collections;

/**
 * Attribute of GoodData project dataset
 */
@JsonTypeName("attribute")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attribute extends NestedAttribute implements Queryable, Updatable {

    @JsonCreator
    private Attribute(@JsonProperty("meta") Meta meta, @JsonProperty("content") NestedAttribute.Content content) {
        super(meta, content);
    }

    /* Just for serialization test */
    Attribute(String title, Key primaryKey, Key foreignKey) {
        this(new Meta(title),  new Content(asList(primaryKey), asList(foreignKey), Collections.<DisplayForm>emptyList(), null, null, null, null, null,
                null, null, null, null, null, null));
    }
}
