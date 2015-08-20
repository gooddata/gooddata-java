package com.gooddata.md;

import com.gooddata.util.BooleanStringDeserializer;
import com.gooddata.util.BooleanStringSerializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Display form of attribute
 */
@JsonTypeName("attributeDisplayForm")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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
        return content.isDefault();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private static class Content extends DisplayForm.Content {

        private final boolean isDefault;

        private Content(@JsonProperty("formOf") String formOf, @JsonProperty("expression") String expression,
                @JsonProperty("default") @JsonDeserialize(using = BooleanStringDeserializer.class) boolean isDefault,
                @JsonProperty("ldmexpression") String ldmExpression,
                @JsonProperty("type") String type) {
            super(formOf, expression, ldmExpression, type);
            this.isDefault = isDefault;
        }

        @JsonProperty("default")
        @JsonSerialize(using = BooleanStringSerializer.class)
        public boolean isDefault() {
            return isDefault;
        }
    }
}
