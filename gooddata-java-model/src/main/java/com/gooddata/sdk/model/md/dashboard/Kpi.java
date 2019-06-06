/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.model.md.AbstractObj;
import com.gooddata.sdk.model.md.Meta;
import com.gooddata.sdk.model.md.Queryable;
import com.gooddata.sdk.model.md.Updatable;
import com.gooddata.sdk.model.md.dashboard.filter.FilterReference;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Represents KPI (key performance indicator) for analytical dashboard.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("kpi")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Kpi extends AbstractObj implements Queryable, Updatable {

    private static final long serialVersionUID = -7747260758488157192L;

    private static final String NONE_COMPARISON_TYPE = "none";

    private final Content content;

    /**
     * Creates new KPI for a given metric with some date filter and comparison
     * @param title title of KPI
     * @param metricUri URI of the KPI metric
     * @param comparisonType KPI comparison type (e.g. {@code "lastYear"})
     * @param comparisonDirection KPI comparison direction (e.g. {@code "growIsGood"})
     * @param ignoreDashboardFilters list of filters which should be ignored for this KPI (can be empty)
     * @param dateDatasetUri KPI date filter dataset URI (optional)
     */
    public Kpi(final String title, final String metricUri, final String comparisonType, final String comparisonDirection,
            final List<FilterReference> ignoreDashboardFilters, final String dateDatasetUri) {
        this(new Meta(title), new Content(
                notEmpty(metricUri, "metricUri"),
                notEmpty(comparisonType, "comparisonType"),
                checkDirection(comparisonType, comparisonDirection),
                null,
                dateDatasetUri,
                notNull(ignoreDashboardFilters, "ignoreDashboardFilters")));
    }

    private static String checkDirection(final String comparisonType, final String comparisonDirection) {
        if (NONE_COMPARISON_TYPE.equalsIgnoreCase(notEmpty(comparisonType, "comparisonType"))) {
            return notEmpty(comparisonDirection, "comparisonDirection");
        } else {
            return comparisonDirection;
        }
    }

    @JsonCreator
    private Kpi(@JsonProperty("meta") final Meta meta, @JsonProperty("content") final Content content) {
        super(meta);
        this.content = content;
    }

    /**
     * @return KPI metric URI string
     */
    @JsonIgnore
    public String getMetricUri() {
        return getContent().getMetric();
    }

    /**
     * @return KPI comparison type
     */
    @JsonIgnore
    public String getComparisonType() {
        return getContent().getComparisonType();
    }

    /**
     * @return KPI comparison direction
     */
    @JsonIgnore
    public String getComparisonDirection() {
        return getContent().getComparisonDirection();
    }

    /**
     * @return KPI date filter dataset URI
     */
    @JsonIgnore
    public String getDateDatasetUri() {
        final String dateDatasetUri = getContent().getDateDataSet();
        return dateDatasetUri != null ? dateDatasetUri : getContent().getDateDimension();
    }

    /**
     * @return list of filter references (containing URIs) of filters which should be ignored for this KPI
     */
    @JsonIgnore
    public List<FilterReference> getIgnoreDashboardFilters() {
        return Collections.unmodifiableList(getContent().getIgnoreDashboardFilters());
    }

    @JsonProperty
    private Content getContent() {
        return content;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Content implements Serializable {

        private final String metric;
        private final String comparisonType;
        private final String comparisonDirection;
        private final String dateDimension;
        private final String dateDataset;
        private final List<FilterReference> ignoreDashboardFilters;

        @JsonCreator
        private Content(
                @JsonProperty("metric") final String metric,
                @JsonProperty("comparisonType") final String comparisonType,
                @JsonProperty("comparisonDirection") final String comparisonDirection,
                @JsonProperty("dateDimension") final String dateDimension,
                @JsonProperty("dateDataSet") final String dateDataset,
                @JsonProperty("ignoreDashboardFilters") final List<FilterReference> ignoreDashboardFilters) {
            this.metric = metric;
            this.comparisonType = comparisonType;
            this.comparisonDirection = comparisonDirection;
            this.dateDimension = dateDimension;
            this.dateDataset = dateDataset;
            this.ignoreDashboardFilters = ignoreDashboardFilters;
        }

        public String getMetric() {
            return metric;
        }

        public String getComparisonType() {
            return comparisonType;
        }

        public String getComparisonDirection() {
            return comparisonDirection;
        }

        /**
         * @return URI of the date dimension
         * @deprecated if not null, {@link #getDateDataSet()} should be used instead
         */
        @Deprecated
        public String getDateDimension() {
            return dateDimension;
        }

        public String getDateDataSet() {
            return dateDataset;
        }

        public List<FilterReference> getIgnoreDashboardFilters() {
            return ignoreDashboardFilters;
        }
    }
}
