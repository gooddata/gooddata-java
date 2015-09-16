/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.gooddata.md.AbstractObj;
import com.gooddata.md.Meta;
import com.gooddata.md.Queryable;
import com.gooddata.md.Updatable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Map;

/**
 * Report definition
 */
@JsonTypeName("reportDefinition")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReportDefinition extends AbstractObj implements Queryable, Updatable {

    private static final String EXPLAIN_LINK = "explain2";
    @JsonProperty("content")
    private final ReportDefinitionContent content;

    @JsonProperty("links")
    private final Map<String, String> links;

    @JsonCreator
    ReportDefinition(@JsonProperty("meta") Meta meta, @JsonProperty("content") ReportDefinitionContent content, @JsonProperty("links") Map<String, String> links) {
        super(meta);
        this.content = content;
        this.links = links;
    }

    ReportDefinition(Meta meta, ReportDefinitionContent content) {
        this(meta, content, null);
    }

    /* Just for serialization test */
    ReportDefinition(String title, ReportDefinitionContent content) {
        super(new Meta(title));
        this.content = content;
        this.links = null;
    }

    @JsonIgnore
    public String getFormat() {
        return content.getFormat();
    }

    @JsonIgnore
    public Grid getGrid() {
        return content.getGrid();
    }

    @JsonIgnore
    public String getExplainLink() {
        return links != null ? links.get(EXPLAIN_LINK) : null;
    }

}
