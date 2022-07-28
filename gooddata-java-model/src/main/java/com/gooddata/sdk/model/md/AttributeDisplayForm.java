/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.sdk.common.util.BooleanDeserializer;
import com.gooddata.sdk.common.util.BooleanIntegerSerializer;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

/**
 * Display form of attribute
 */
@JsonTypeName("attributeDisplayForm")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeDisplayForm extends DisplayForm implements Updatable {

    private static final long serialVersionUID = -7903851496647992573L;

    @JsonProperty("content")
    protected final AttributeContent attributeContent;

    @JsonCreator
    private AttributeDisplayForm(@JsonProperty("meta") Meta meta, @JsonProperty("content") AttributeContent content,
            @JsonProperty("links") Links links) {
        super(meta, content, links);
        this.attributeContent = content;
    }

    /* Just for serialization test */
    AttributeDisplayForm(String title, String formOf, String expression, boolean isDefault, String ldmExpression, String type, String elements) {
        this(new Meta(title), new AttributeContent(formOf, expression, isDefault, ldmExpression, type), new Links(elements));
    }

    @JsonIgnore
    public boolean isDefault() {
        return Boolean.TRUE.equals(attributeContent.isDefault());
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class AttributeContent extends DisplayForm.Content {

        private static final long serialVersionUID = -8502672468934478137L;
        private final boolean isDefault;

        private AttributeContent(@JsonProperty("formOf") String formOf, @JsonProperty("expression") String expression,
                                 @JsonProperty("default") @JsonDeserialize(using = BooleanDeserializer.class) Boolean isDefault,
                                 @JsonProperty("ldmexpression") String ldmExpression,
                                 @JsonProperty("type") String type) {
            super(formOf, expression, ldmExpression, type);
            this.isDefault = isDefault;
        }

        @JsonProperty("default")
        @JsonSerialize(using = BooleanIntegerSerializer.class)
        public Boolean isDefault() {
            return isDefault;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
