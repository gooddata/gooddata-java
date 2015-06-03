package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Display form of attribute
 */
@JsonTypeName("attributeDisplayForm")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AttributeDisplayForm extends DisplayForm implements Updatable {

    @JsonCreator
    private AttributeDisplayForm(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta, content);
    }

    /* Just for serialization test */
    AttributeDisplayForm(String title, String formOf, String expression, boolean isDefault, String ldmExpression) {
        super(new Meta(title), new Content(formOf, expression, isDefault, ldmExpression));
    }
}
