/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collection;

/**
 * Metadata query result
 */
@JsonTypeName("query")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Query {

    public static final String URI = "/gdc/md/{projectId}/query/{type}";

    private final Collection<Entry> entries;

    private final Meta meta;

    @JsonCreator
    private Query(@JsonProperty("entries") Collection<Entry> entries, @JsonProperty("meta") Meta meta) {
        this.entries = entries;
        this.meta = meta;
    }

    public Collection<Entry> getEntries() {
        return entries;
    }

    public String getCategory() {
        return meta.getCategory();
    }

    public String getSummary() {
        return meta.getSummary();
    }

    public String getTitle() {
        return meta.getTitle();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Meta {
        private final String category;
        private final String summary;
        private final String title;

        @JsonCreator
        public Meta(@JsonProperty("category") String category, @JsonProperty("summary") String summary,
                    @JsonProperty("title") String title) {
            this.category = category;
            this.summary = summary;
            this.title = title;
        }

        public String getCategory() {
            return category;
        }

        public String getSummary() {
            return summary;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }

}
