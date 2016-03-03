/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.gdc.UriResponse;
import com.gooddata.util.BooleanIntegerDeserializer;

/**
 * Represents datasets' loading column.
 * Deserialization only.
 */
@JsonTypeName("dataLoadingColumn")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataLoadingColumn extends AbstractObj implements Queryable {

    private static final String INT = "INT";
    private static final String VARCHAR = "VARCHAR";

    private final Content content;

    @JsonCreator
    private DataLoadingColumn(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    public UriResponse getColumnUri() {
        return content.getColumnUri();
    }

    public String getName() {
        return content.getColumnName();
    }

    public String getType() {
        return content.getColumnType();
    }

    /**
     * @return true when the type is not null and equal to <code>VARCHAR</code>, false otherwise
     */
    public boolean hasTypeVarchar() {
        return VARCHAR.equals(getType());
    }

    /**
     * @return true when the type is not null and equal to <code>INT</code>, false otherwise
     */
    public boolean hasTypeInt() {
        return INT.equals(getType());
    }

    public Integer getLength() {
        return content.getColumnLength();
    }

    public Integer getPrecision() {
        return content.getColumnPrecision();
    }

    public boolean isUnique() {
        return content.isColumnUnique();
    }

    public boolean isNullable() {
        return content.isColumnNull();
    }

    public String getSynchronizeType() {
        return content.getColumnSynchronize() !=  null ? content.getColumnSynchronize().getType() : null;
    }

    public Integer getSynchronizeLength() {
        return content.getColumnSynchronize() !=  null ? content.getColumnSynchronize().getLength() : null;
    }

    public Integer getSynchronizePrecision() {
        return content.getColumnSynchronize() !=  null ? content.getColumnSynchronize().getPrecision() : null;
    }

    private static class Content {

        private final UriResponse columnUri;
        private final String columnName;
        private final String columnType;
        private final Integer columnLength;
        private final Integer columnPrecision;
        private final boolean columnUnique;
        private final boolean columnNull;
        private final ColumnSynchronize columnSynchronize;

        private Content(@JsonProperty("column") UriResponse columnUri, @JsonProperty("columnName") String columnName, @JsonProperty("type") String columnType,
                @JsonProperty("length") Integer columnLength, @JsonProperty("precision") Integer columnPrecision,
                @JsonProperty("columnUnique") @JsonDeserialize(using = BooleanIntegerDeserializer.class) boolean columnUnique,
                @JsonProperty("columnNull") @JsonDeserialize(using = BooleanIntegerDeserializer.class) boolean columnNull,
                @JsonProperty("columnSynchronize") ColumnSynchronize columnSynchronize) {
            this.columnUri = columnUri;
            this.columnName = columnName;
            this.columnType = columnType;
            this.columnLength = columnLength;
            this.columnPrecision = columnPrecision;
            this.columnUnique = columnUnique;
            this.columnNull = columnNull;
            this.columnSynchronize = columnSynchronize;
        }

        public UriResponse getColumnUri() {
            return columnUri;
        }

        public String getColumnName() {
            return columnName;
        }

        public String getColumnType() {
            return columnType;
        }

        public Integer getColumnLength() {
            return columnLength;
        }

        public Integer getColumnPrecision() {
            return columnPrecision;
        }

        public boolean isColumnUnique() {
            return columnUnique;
        }

        public boolean isColumnNull() {
            return columnNull;
        }

        public ColumnSynchronize getColumnSynchronize() {
            return columnSynchronize;
        }
    }

    private static class ColumnSynchronize {
        private final String type;
        private final Integer length;
        private final Integer precision;

        @JsonCreator
        private ColumnSynchronize(@JsonProperty("columnType") String type, @JsonProperty("columnLength") Integer length,
                @JsonProperty("precision") Integer precision) {
            this.type = type;
            this.length = length;
            this.precision = precision;
        }

        public String getType() {
            return type;
        }

        public Integer getLength() {
            return length;
        }

        public Integer getPrecision() {
            return precision;
        }
    }
}
