/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Metric
 */
@JsonTypeName("metric")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Metric extends AbstractObj implements Queryable {

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

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Content {
        private final String expression;
        private final String format;

        @JsonCreator
        public Content(@JsonProperty("expression") String expression, @JsonProperty("format") String format) {
            this.expression = expression;
            this.format = format;
        }

        public String getExpression() {
            return expression;
        }

        public String getFormat() {
            return format;
        }
    }
}
