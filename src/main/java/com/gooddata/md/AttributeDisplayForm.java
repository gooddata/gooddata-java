package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.util.BooleanDeserializer;
import com.gooddata.util.BooleanIntegerSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Display form of attribute
 */
@JsonTypeName("attributeDisplayForm")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeDisplayForm extends DisplayForm implements Updatable {

    @JsonProperty("content")
    protected final Content content;

    @JsonCreator
    private AttributeDisplayForm(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content,
            @JsonProperty("links") Links links) {
        super(meta, content, links);
        this.content = content;
    }

    /* Just for serialization test */
    AttributeDisplayForm(String title, String formOf, String expression, boolean isDefault, String ldmExpression, String type, String elements) {
        this(new Meta(title), new Content(formOf, expression, isDefault, ldmExpression, type), new Links(elements));
    }

    @JsonIgnore
    public boolean isDefault() {
        return Boolean.TRUE.equals(content.isDefault());
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Content extends DisplayForm.Content {

        private final boolean isDefault;

        private Content(@JsonProperty("formOf") String formOf, @JsonProperty("expression") String expression,
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
    }
}
