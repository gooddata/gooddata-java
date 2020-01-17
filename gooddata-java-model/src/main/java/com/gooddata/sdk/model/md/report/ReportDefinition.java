/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import static com.gooddata.sdk.common.util.Validate.notNullState;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.sdk.model.md.AbstractObj;
import com.gooddata.sdk.model.md.Meta;
import com.gooddata.sdk.model.md.Queryable;
import com.gooddata.sdk.model.md.Updatable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Map;

/**
 * Report definition
 */
@JsonTypeName("reportDefinition")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportDefinition extends AbstractObj implements Queryable, Updatable {

    private static final long serialVersionUID = 9069611345896541468L;
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
    public String getExplainUri() {
        return notNullState(links, "links").get(EXPLAIN_LINK);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
