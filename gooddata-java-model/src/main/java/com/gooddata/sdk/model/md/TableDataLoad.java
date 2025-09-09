/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;

/**
 * Represents data load of physical table.
 * Deserialization only.
 */
@JsonTypeName("tableDataLoad")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class TableDataLoad extends AbstractObj implements Queryable {

    private static final long serialVersionUID = -5209417147612785042L;
    public static final String TYPE_FULL = "full";
    public static final String TYPE_INCREMENTAL = "incremental";

    private final Content content;

    @JsonCreator
    private TableDataLoad(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    /**
     * @return location of load data source
     */
    public String getDataSourceLocation() {
        return content.getDataSourceLocation();
    }

    /**
     * @return true if the type is <code>full</code>, false otherwise
     */
    public boolean isFull() {
        return TYPE_FULL.equals(getType());
    }

    /**
     * @return true if the type is <code>incremental</code>, false otherwise
     */
    public boolean isIncremental() {
        return TYPE_INCREMENTAL.equals(getType());
    }

    /**
     * @return type of the load, one of <code>full,incremental</code>
     */
    public String getType() {
        return content.getTypeOfLoad();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Content implements Serializable {

        private static final long serialVersionUID = 3566067131546271394L;
        private final String dataSourceLocation;
        private final String typeOfLoad;

        @JsonCreator
        private Content(@JsonProperty("dataSourceLocation") String dataSourceLocation, @JsonProperty("typeOfLoad") String typeOfLoad) {
            this.dataSourceLocation = dataSourceLocation;
            this.typeOfLoad = typeOfLoad;
        }

        public String getDataSourceLocation() {
            return dataSourceLocation;
        }

        public String getTypeOfLoad() {
            return typeOfLoad;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}

