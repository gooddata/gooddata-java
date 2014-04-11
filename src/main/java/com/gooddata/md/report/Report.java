/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.gooddata.md.Meta;
import com.gooddata.md.Obj;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;

import static java.util.Arrays.asList;

/**
 */
@JsonTypeName("report")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Report extends Obj {

    private final Content content;

    @JsonCreator
    public Report(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    public Report(String title, ReportDefinition definition) {
        this(new Meta(title), new Content(asList(definition.getMeta().getUri())));
    }

    public Content getContent() {
        return content;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public static class Content {
        private final Collection<String> definitions;

        @JsonCreator
        public Content(@JsonProperty("definitions") Collection<String> definitions) {
            this.definitions = definitions;
        }

        public Collection<String> getDefinitions() {
            return definitions;
        }
    }
}
