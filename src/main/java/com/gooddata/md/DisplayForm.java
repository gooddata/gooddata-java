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
    protected final Content content;

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
    public String getLdmExpression() {
        return content.getLdmExpression();
    }

    @JsonIgnore
    public String getType() {
        return content.getType();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    protected static class Content {

        private final String formOf;
        private final String expression;
        private final String ldmExpression;
        private final String type;

        @JsonCreator
        public Content(@JsonProperty("formOf") String formOf, @JsonProperty("expression") String expression,
                       @JsonProperty("ldmexpression") String ldmExpression, @JsonProperty("type") String type) {
            this.formOf = formOf;
            this.expression = expression;
            this.ldmExpression = ldmExpression;
            this.type = type;
        }

        public String getFormOf() {
            return formOf;
        }

        public String getExpression() {
            return expression;
        }

        @JsonProperty("ldmexpression")
        public String getLdmExpression() {
            return ldmExpression;
        }

        public String getType() {
            return type;
        }
    }

}
