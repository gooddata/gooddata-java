/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents metadata dimension.
 */
@JsonTypeName("dimension")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dimension extends AbstractObj implements Queryable, Updatable {

    private static final long serialVersionUID = -6667238416764957848L;

    @JsonProperty("content")
    private final Content content;

    @JsonCreator
    private Dimension(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    /* for serialization test */
    Dimension(String title) {
        this(new Meta(title), new Content(Collections.emptyList()));
    }

    @JsonIgnore
    public Collection<NestedAttribute> getAttributes() {
        return content.getAttributes();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    private static class Content implements Serializable {

        private static final long serialVersionUID = -6382409825359596163L;

        @JsonProperty("attributes")
        private final Collection<NestedAttribute> attributes;

        @JsonCreator
        public Content(@JsonProperty("attributes") Collection<NestedAttribute> attributes) {
            this.attributes = attributes;
        }

        public Collection<NestedAttribute> getAttributes() {
            return attributes;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
