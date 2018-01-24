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

import java.io.Serializable;
import java.util.Collection;

/**
 * Represents physical data model table.
 * Deserialization only.
 */
@JsonTypeName("table")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class Table extends AbstractObj implements Queryable {

    private static final long serialVersionUID = 8188850708608710066L;
    private final Content content;

    @JsonCreator
    private Table(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    /**
     * @return name of the table in DB
     */
    public String getDBName() {
        return content.getTableDBName();
    }

    /**
     * @return collection of {@link TableDataLoad}'s uris, can return null
     */
    public Collection<String> getDataLoads() {
        return content.getTableDataLoads();
    }

    /**
     * @return table weight, can return null
     */
    public Integer getWeight() {
        return content.getWeight();
    }

    /**
     * @return uri of active {@link TableDataLoad}, can return null
     */
    public String getActiveDataLoad() {
        return content.getActiveDataLoad();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Content implements Serializable {

        private static final long serialVersionUID = -3487498890189149368L;
        private final String tableDBName;
        private final String activeDataLoad;
        private final Collection<String> tableDataLoads;
        private final Integer weight;

        @JsonCreator
        private Content(@JsonProperty("tableDBName") String tableDBName, @JsonProperty("activeDataLoad") String activeDataLoad,
                @JsonProperty("tableDataLoad") Collection<String> tableDataLoads, @JsonProperty("weight") Integer weight) {
            this.tableDBName = tableDBName;
            this.activeDataLoad = activeDataLoad;
            this.tableDataLoads = tableDataLoads;
            this.weight = weight;
        }

        public String getTableDBName() {
            return tableDBName;
        }

        public String getActiveDataLoad() {
            return activeDataLoad;
        }

        public Collection<String> getTableDataLoads() {
            return tableDataLoads;
        }

        public Integer getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
