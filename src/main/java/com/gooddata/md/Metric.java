/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Collection;

/**
 * Metric
 */
@JsonTypeName("metric")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("links")
public class Metric extends AbstractObj implements Queryable, Updatable {

    @JsonProperty("content")
    private final Content content;

    @JsonCreator
    private Metric(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    public Metric(String title, String expression, String format) {
        this(new Meta(title), new Metric.Content(expression, format));
    }

    @JsonIgnore
    public String getExpression() {
        return content.getExpression();
    }

    @JsonIgnore
    public String getFormat() {
        return content.getFormat();
    }

    @JsonIgnore
    public MaqlAst getMaqlAst() {
        return content.getMaqlAst();
    }

    /**
     * URIs of folders containing this object
     * @return collection of URIs or null
     */
    @JsonIgnore
    public Collection<String> getFolders() {
        return content.getFolders();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Content {

        private final String expression;

        @JsonProperty("format")
        private String format;

        @JsonProperty("tree")
        private MaqlAst maqlAst;

        private final Collection<String> folders;

        @JsonCreator
        public Content(@JsonProperty("expression") String expression, @JsonProperty("folders") Collection<String> folders) {
            this.expression = expression;
            this.folders = folders;
        }

        public Content(final String expression, final String format) {
            this.expression = expression;
            this.format = format;
            this.folders = null;
        }

        public String getExpression() {
            return expression;
        }

        public void setFormat(final String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }

        public void setMaqlAst(final MaqlAst maqlAst) {
            this.maqlAst = maqlAst;
        }

        public MaqlAst getMaqlAst() {
            return maqlAst;
        }

        public Collection<String> getFolders() {
            return folders;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
