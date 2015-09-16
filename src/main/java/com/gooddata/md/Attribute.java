/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import static java.util.Arrays.asList;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collections;

/**
 * Attribute of GoodData project dataset
 */
@JsonTypeName("attribute")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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
