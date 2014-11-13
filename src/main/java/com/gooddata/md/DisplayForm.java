/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.gooddata.util.BooleanIntegerDeserializer;
import com.gooddata.util.BooleanIntegerSerializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Display form
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DisplayForm extends AbstractObj {

    @JsonProperty("content")
    private final Content content;

    @JsonCreator
    protected DisplayForm(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    @JsonIgnore
    public String getFormOf() {
        return content.getFormOf();
    }

    @JsonIgnore
    public String getExpression() {
        return content.getExpression();
    }

    @JsonIgnore
    public boolean isDefault() {
        return content.isDefault();
    }

    @JsonIgnore
    public String getLdmExpression() {
        return content.getLdmExpression();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    protected static class Content {

        private final String formOf;
        private final String expression;
        private final boolean isDefault;
        private final String ldmExpression;

        @JsonCreator
        public Content(@JsonProperty("formOf") String formOf, @JsonProperty("expression") String expression,
                       @JsonProperty("default") @JsonDeserialize(using = BooleanIntegerDeserializer.class) boolean isDefault,
                       @JsonProperty("ldmexpression") String ldmExpression) {
            this.formOf = formOf;
            this.expression = expression;
            this.isDefault = isDefault;
            this.ldmExpression = ldmExpression;
        }

        public String getFormOf() {
            return formOf;
        }

        public String getExpression() {
            return expression;
        }

	    @JsonProperty("default")
	    @JsonSerialize(using = BooleanIntegerSerializer.class)
        public boolean isDefault() {
            return isDefault;
        }

	    @JsonProperty("ldmexpression")
        public String getLdmExpression() {
            return ldmExpression;
        }
    }

}
