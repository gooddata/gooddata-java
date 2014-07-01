/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.gooddata.md.Meta;
import com.gooddata.md.Obj;
import com.gooddata.md.Queryable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * Report
 */
@JsonTypeName("report")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Report extends Obj implements Queryable {

    @JsonProperty("content")
    private final Content content;

    @JsonCreator
    private Report(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    public Report(String title, String definition, String domain) {
        super(new Meta(title));
        content = new Content(asList(definition), asList(domain));
    }

    @JsonIgnore
    public Collection<String> getDefinitions() {
        return content.getDefinitions();
    }

    @JsonIgnore
    public Collection<String> getDomains() {
        return content.getDomains();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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
