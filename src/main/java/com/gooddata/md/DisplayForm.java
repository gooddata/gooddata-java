/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Display form
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DisplayForm extends AbstractObj {

    @JsonProperty("content")
    protected final Content content;

    @JsonProperty("links")
    private final Links links;

    @JsonCreator
    protected DisplayForm(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content,
            @JsonProperty("links") Links links) {
        super(meta);
        this.content = content;
        this.links = links;
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

    @JsonIgnore
    public String getElementsLink() {
        return links.getElements();
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class Links {
        private final String elements;

        @JsonCreator
        protected Links(@JsonProperty("elements") String elements) {
            this.elements = elements;
        }

        public String getElements() {
            return elements;
        }
    }

}
