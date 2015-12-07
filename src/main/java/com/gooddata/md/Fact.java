/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Collection;

import static java.util.Collections.singletonList;

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
    Fact(String title, String data, String type, String folder) {
        super(new Meta(title));
        content = new Content(singletonList(new Expression(data, type)), singletonList(folder));
    }

    @JsonIgnore
    public Collection<Expression> getExpressions() {
        return content.getExpression();
    }

    @JsonIgnore
    public Collection<String> getFolders() {
        return content.getFolders();
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private static class Content {

        @JsonProperty("expr")
        private final Collection<Expression> expression;

        @JsonProperty("folders")
        private final Collection<String> folders;

        @JsonCreator
        public Content(@JsonProperty("expr") Collection<Expression> expression, @JsonProperty("folders") Collection<String> folders) {
            this.expression = expression;
            this.folders = folders;
        }

        public Collection<Expression> getExpression() {
            return expression;
        }

        public Collection<String> getFolders() {
            return folders;
        }
    }
}
