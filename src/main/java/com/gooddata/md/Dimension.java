/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents metadata dimension.
 */
@JsonTypeName("dimension")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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

    @JsonIgnoreProperties(ignoreUnknown = true)
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
