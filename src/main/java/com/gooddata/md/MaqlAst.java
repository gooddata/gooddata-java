/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Arrays;

/**
 * MAQL AST representation
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MaqlAst {

    private final String type;
    private final MaqlAstPosition position;

    @JsonProperty("content")
    private MaqlAst[] content;

    /**
     * STRING | DATE | INT | FLOAT
     */
    @JsonProperty("value")
    private String value;

    @JsonCreator
    public MaqlAst(@JsonProperty("type") final String type, @JsonProperty("position") final MaqlAstPosition position) {
        this.type = type;
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public MaqlAstPosition getPosition() {
        return position;
    }

    public MaqlAst[] getContent() {
        return content == null ? null : Arrays.copyOf(content, content.length);
    }

    public void setContent(final MaqlAst[] content) {
        this.content = content == null ? null : Arrays.copyOf(content, content.length);
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    public static class MaqlAstPosition {

        private final int line;
        private final int column;

        @JsonCreator
        public MaqlAstPosition(@JsonProperty("column") final int column, @JsonProperty("line") final int line) {
            this.column = column;
            this.line = line;
        }

        public int getColumn() {
            return column;
        }

        public int getLine() {
            return line;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}

