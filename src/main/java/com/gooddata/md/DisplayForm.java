/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Display form
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DisplayForm extends Obj {

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
    public Integer getDefault() {
        return content.getDefault();
    } //TODO boolean

    @JsonIgnore
    public String getLdmExpression() {
        return content.getLdmExpression();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    protected static class Content {

        private final String formOf;
        private final String expression;
        @JsonProperty("default")
        private final Integer isDefault;
        @JsonProperty("ldmexpression")
        private final String ldmExpression;

        @JsonCreator
        public Content(@JsonProperty("formOf") String formOf, @JsonProperty("expression") String expression,
                       @JsonProperty("default") Integer isDefault,
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

        public Integer getDefault() {
            return isDefault;
        }

        public String getLdmExpression() {
            return ldmExpression;
        }
    }

}
