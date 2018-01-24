/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.md.AbstractObj;
import com.gooddata.md.Meta;
import com.gooddata.md.Queryable;
import com.gooddata.md.Updatable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * Report
 */
@JsonTypeName("report")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report extends AbstractObj implements Queryable, Updatable {

    private static final long serialVersionUID = 189440633045112981L;

    @JsonProperty("content")
    private final Content content;

    @JsonCreator
    private Report(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    /**
     * Creates new report with given title and definitions
     * @param title report title
     * @param definitions report definitions
     */
    public Report(String title, ReportDefinition... definitions) {
        // domains must be empty list to be included in serialized form properly
        this(new Meta(title), new Content(asList(uris(definitions)), emptyList()));
    }

    @JsonIgnore
    public Collection<String> getDefinitions() {
        return content.getDefinitions();
    }

    @JsonIgnore
    public Collection<String> getDomains() {
        return content.getDomains();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    private static class Content implements Serializable {

        private static final long serialVersionUID = 3437452191500594445L;
        private final Collection<String> definitions;
        private final Collection<String> domains;

        @JsonCreator
        public Content(@JsonProperty("definitions") Collection<String> definitions,
                       @JsonProperty("domains") Collection<String> domains) {
            this.definitions = definitions;
            this.domains = domains;
        }

        public Collection<String> getDefinitions() {
            return definitions;
        }

        public Collection<String> getDomains() {
            return domains;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
