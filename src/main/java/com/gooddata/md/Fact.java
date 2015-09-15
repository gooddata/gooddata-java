/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * Fact of GoodData project dataset
 */
@JsonTypeName("fact")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Fact extends AbstractObj implements Queryable, Updatable {

    @JsonProperty("content")
    private final Content content;

    @JsonCreator
    private Fact(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    /* Just for serialization test */
    Fact(String title, String data, String type) {
        super(new Meta(title));
        content = new Content(asList(new Expression(data, type)));
    }

    @JsonIgnore
    public Collection<Expression> getExpressions() {
        return content.getExpression();
    }

    private static class Content {

        @JsonProperty("expr")
        private final Collection<Expression> expression;

        @JsonCreator
        public Content(@JsonProperty("expr") Collection<Expression> expression) {
            this.expression = expression;
        }

        public Collection<Expression> getExpression() {
            return expression;
        }
    }
}
