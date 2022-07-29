/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.common.util.BooleanDeserializer;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;

/**
 * Represents datasets' loading column.
 * Deserialization only.
 */
@JsonTypeName("dataLoadingColumn")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataLoadingColumn extends AbstractObj implements Queryable {

    private static final long serialVersionUID = -1280718594585386535L;
    private static final String INT = "INT";
    private static final String VARCHAR = "VARCHAR";

    private final Content content;

    @JsonCreator
    private DataLoadingColumn(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    public String getColumnUri() {
        return content.getColumnUri().getUri();
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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    private static class Content implements Serializable {

        private static final long serialVersionUID = 3821238884831793602L;
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
                @JsonProperty("columnUnique") @JsonDeserialize(using = BooleanDeserializer.class) boolean columnUnique,
                @JsonProperty("columnNull") @JsonDeserialize(using = BooleanDeserializer.class) boolean columnNull,
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

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }

    private static class ColumnSynchronize implements Serializable {
        private final String type;
        private final Integer length;
        private final Integer precision;

        @JsonCreator
        private ColumnSynchronize(@JsonProperty("columnType") String type, @JsonProperty("columnLength") Integer length,
                @JsonProperty("columnPrecision") Integer precision) {
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

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
