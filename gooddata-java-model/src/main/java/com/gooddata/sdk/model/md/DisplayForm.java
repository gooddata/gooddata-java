/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;

/**
 * Display form
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DisplayForm extends AbstractObj {

    private static final long serialVersionUID = 8719802319193893780L;

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
    public String getElementsUri() {
        return links.getElements();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected static class Content implements Serializable {

        private static final long serialVersionUID = 6865880678569437635L;
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

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class Links implements Serializable {

        private static final long serialVersionUID = 1704085675250093860L;
        private final String elements;

        @JsonCreator
        protected Links(@JsonProperty("elements") String elements) {
            this.elements = elements;
        }

        public String getElements() {
            return elements;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }

}
