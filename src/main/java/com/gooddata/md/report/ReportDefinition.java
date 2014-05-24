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

/**
 * Report definition
 */
@JsonTypeName("reportDefinition")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReportDefinition extends Obj implements Queryable {

    @JsonProperty("content")
    private final ReportDefinitionContent content;

    @JsonCreator
    ReportDefinition(@JsonProperty("meta") Meta meta, @JsonProperty("content") ReportDefinitionContent content) {
        super(meta);
        this.content = content;
    }

    /* Just for serialization test */
    ReportDefinition(String title, ReportDefinitionContent content) {
        super(new Meta(title));
        this.content = content;
    }

    @JsonIgnore
    public String getFormat() {
        return content.getFormat();
    }

    @JsonIgnore
    public Grid getGrid() {
        return content.getGrid();
    }

}
