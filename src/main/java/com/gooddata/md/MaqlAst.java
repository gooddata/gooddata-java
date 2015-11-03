/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * MAQL AST representation
 */
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
        return content;
    }

    public void setContent(final MaqlAst[] content) {
        this.content = content;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
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
    }
}

