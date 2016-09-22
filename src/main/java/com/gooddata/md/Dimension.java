/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents metadata dimension.
 */
@JsonTypeName("dimension")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dimension extends AbstractObj implements Queryable, Updatable {

    @JsonProperty("content")
    private final Content content;

    @JsonCreator
    private Dimension(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    /* for serialization test */
    Dimension(String title) {
        this(new Meta(title), new Content(Collections.<NestedAttribute>emptyList()));
    }

    @JsonIgnore
    public Collection<NestedAttribute> getAttributes() {
        return content.getAttributes();
    }

    private static class Content {

        @JsonProperty("attributes")
        private final Collection<NestedAttribute> attributes;

        @JsonCreator
        public Content(@JsonProperty("attributes") Collection<NestedAttribute> attributes) {
            this.attributes = attributes;
        }

        public Collection<NestedAttribute> getAttributes() {
            return attributes;
        }
    }
}
