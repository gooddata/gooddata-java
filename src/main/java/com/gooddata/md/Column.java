/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

/**
 * Represents physical data model column. Doesn't implement all fields right now.
 * Deserialization only.
 */
@JsonTypeName("column")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class Column extends AbstractObj implements Queryable {

    public static final String TYPE_PK = "pk";
    public static final String TYPE_INPUT_PK = "inputpk";
    public static final String TYPE_FK = "fk";
    public static final String TYPE_FACT = "fact";
    public static final String TYPE_DISPLAY_FORM = "displayForm";

    private final Content content;

    private Column(@JsonProperty("meta") Meta meta, @JsonProperty("content")Content content) {
        super(meta);
        this.content = content;
    }

    /**
     * @return uri of physical {@link Table}
     */
    public String getTableUri() {
        return content.getTable();
    }

    /**
     * @return type of column, one of <code>pk,inputpk,fk,fact,displayForm</code> or null
     */
    public String getType() {
        return content.getColumnType();
    }

    /**
     * @return name of the column in DB
     */
    public String getDBName() {
        return content.getColumnDBName();
    }

    /**
     * @return true when type is <code>pk</code>, false otherwise
     */
    public boolean isPk() {
        return TYPE_PK.equals(getType());
    }

    /**
     * @return true when type is <code>inputpk</code>, false otherwise
     */
    public boolean isInputPk() {
        return TYPE_INPUT_PK.equals(getType());
    }

    /**
     * @return true when type is <code>fk</code>, false otherwise
     */
    public boolean isFk() {
        return TYPE_FK.equals(getType());
    }

    /**
     * @return true when type is <code>fact</code>, false otherwise
     */
    public boolean isFact() {
        return TYPE_FACT.equals(getType());
    }

    /**
     * @return true when type is <code>pk</code>, false otherwise
     */
    public boolean isDisplayForm() {
        return TYPE_DISPLAY_FORM.equals(getType());
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Content {
        private final String table;
        private final String columnDBName;
        private final String columnType;

        @JsonCreator
        private Content(@JsonProperty("table") String table, @JsonProperty("columnDBName") String columnDBName,
                @JsonProperty("columnType") String columnType) {
            this.table = table;
            this.columnDBName = columnDBName;
            this.columnType = columnType;
        }

        public String getTable() {
            return table;
        }

        public String getColumnDBName() {
            return columnDBName;
        }

        public String getColumnType() {
            return columnType;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
