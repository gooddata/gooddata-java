/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.model.md.Metric;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Metric used in {@link Grid} for report definition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetricElement implements Serializable {

    private static final long serialVersionUID = 6199743301553304055L;
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
     * Creates new instance using uri of given metric and alias.
     *
     * @param metric metric to create element from
     * @param alias  metric alias
     */
    public MetricElement(final Metric metric, final String alias) {
        this(notNull(notNull(metric, "metric").getUri(), "uri"), notNull(alias, "alias"), null, null);
    }

    /**
     * Creates new instance using uri of given metric and it's title as alias.
     *
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
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
