/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.Collection;

import static java.util.Collections.singletonList;

/**
 * Fact of GoodData project dataset
 */
@JsonTypeName("fact")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fact extends AbstractObj implements Queryable, Updatable {

    private static final long serialVersionUID = 1394960609414171940L;

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
    private static class Content implements Serializable {

        private static final long serialVersionUID = 2141254685536566363L;

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

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
