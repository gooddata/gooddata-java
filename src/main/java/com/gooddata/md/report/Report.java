/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
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

import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * Report
 */
@JsonTypeName("report")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report extends AbstractObj implements Queryable, Updatable {

    @JsonProperty("content")
    private final Content content;

    @JsonCreator
    private Report(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    /* Just for serialization test */
    Report(String title, String domain, String... definitions) {
        super(new Meta(title));
        content = new Content(asList(definitions), asList(domain));
    }

    public Report(String title, ReportDefinition... definitions) {
        this(title, null, uris(definitions));
    }

    @JsonIgnore
    public Collection<String> getDefinitions() {
        return content.getDefinitions();
    }

    @JsonIgnore
    public Collection<String> getDomains() {
        return content.getDomains();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Content {
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
    }
}
