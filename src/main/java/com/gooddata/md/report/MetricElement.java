/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Metric used in {@link Grid} for report definition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetricElement {

    private final String uri;
    private final String alias;
    private final String format;
    private final String drillAcrossStepAttributeDisplayFormUri;

    @JsonCreator
    public MetricElement(@JsonProperty("uri") final String uri,
                         @JsonProperty("alias") final String alias,
                         @JsonProperty("format") final String format,
                         @JsonProperty("drillAcrossStepAttributeDF") String drillAcrossStepAttributeDisplayFormUri) {
        this.uri = uri;
        this.alias = alias;
        this.format = format;
        this.drillAcrossStepAttributeDisplayFormUri = drillAcrossStepAttributeDisplayFormUri;
    }

    public MetricElement(String uri, String alias) {
        this(uri, alias, null, null);
    }

    public String getUri() {
        return uri;
    }

    public String getAlias() {
        return alias;
    }

    public String getFormat() {
        return format;
    }

    @JsonProperty("drillAcrossStepAttributeDF")
    public String getDrillAcrossStepAttributeDisplayFormUri() {
        return drillAcrossStepAttributeDisplayFormUri;
    }
}
