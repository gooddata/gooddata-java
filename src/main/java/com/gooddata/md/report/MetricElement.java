/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.md.Metric;
import com.gooddata.util.GoodDataToStringBuilder;

import static com.gooddata.util.Validate.notNull;

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
    MetricElement(@JsonProperty("uri") final String uri,
                         @JsonProperty("alias") final String alias,
                         @JsonProperty("format") final String format,
                         @JsonProperty("drillAcrossStepAttributeDF") String drillAcrossStepAttributeDisplayFormUri) {
        this.uri = uri;
        this.alias = alias;
        this.format = format;
        this.drillAcrossStepAttributeDisplayFormUri = drillAcrossStepAttributeDisplayFormUri;
    }

    /**
     * Creates a new metric element
     * @param uri metric uri
     * @param alias metric alias
     * @deprecated for compatibility with 1.x only,
     * use {@link #MetricElement(Metric, String)} or {@link #MetricElement(Metric)}
     */
    @Deprecated
    public MetricElement(String uri, String alias) {
        this(uri, alias, null, null);
    }

    /**
     * Creates new instance using uri of given metric and alias.
     * @param metric metric to create element from
     * @param alias metric alias
     */
    public MetricElement(final Metric metric, final String alias) {
        this(notNull(notNull(metric, "metric").getUri(), "uri"), notNull(alias, "alias"), null, null);
    }

    /**
     * Creates new instance using uri of given metric and it's title as alias.
     * @param metric metric to create element from
     */
    public MetricElement(final Metric metric) {
        this(metric, notNull(metric, "metric").getTitle());
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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.toStringExclude(this);
    }
}
