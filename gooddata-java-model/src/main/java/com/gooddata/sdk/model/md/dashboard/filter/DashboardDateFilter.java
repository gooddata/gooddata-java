/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard.filter;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Date filter located on analytical dashboard.
 * Is not standalone metadata object - always must be part of {@link DashboardFilterContext}.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(DashboardDateFilter.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardDateFilter implements DashboardFilter {

    public static final String RELATIVE_FILTER_TYPE = "relative";
    public static final String ABSOLUTE_FILTER_TYPE = "absolute";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final String ABSOLUTE_DATE_FILTER_GRANULARITY = "GDC.time.date";

    static final String NAME = "dateFilter";

    private final String from;
    private final String to;
    private final String granularity;
    private final String dataset;
    private final String type;

    @JsonCreator
    private DashboardDateFilter(
            @JsonProperty("from") final String from,
            @JsonProperty("to") final String to,
            @JsonProperty("granularity") final String granularity,
            @JsonProperty("dataSet") final String dataset,
            @JsonProperty("type") final String type) {
        this.from = from;
        this.to = to;
        this.granularity = granularity;
        this.dataset = dataset;
        this.type = type;
    }

    /**
     * Creates relative date filter with the given interval and granularity.
     *
     * @param from interval from
     * @param to interval to
     * @param granularity granularity (e.g. {@code GDC.time.year})
     * @param datasetUri date dataset URI (optional)
     * @return created filter
     */
    @JsonIgnore
    public static DashboardDateFilter relativeDateFilter(final int from, final int to, final String granularity, final String datasetUri) {
        return new DashboardDateFilter(
                Integer.toString(from),
                Integer.toString(to),
                notEmpty(granularity, "granularity"),
                datasetUri,
                RELATIVE_FILTER_TYPE);
    }

    /**
     * Creates absolute date filter with the given interval
     *
     * @param from interval from
     * @param to interval to
     * @param datasetUri date dataset URI (optional)
     * @return created filter
     */
    @JsonIgnore
    public static DashboardDateFilter absoluteDateFilter(final LocalDate from, final LocalDate to, final String datasetUri) {
        return new DashboardDateFilter(
                notNull(from, "from").format(DATE_FORMAT),
                notNull(to, "to").format(DATE_FORMAT),
                ABSOLUTE_DATE_FILTER_GRANULARITY,
                datasetUri,
                ABSOLUTE_FILTER_TYPE);
    }

    /**
     * Returns <b>from</b> value of date filter interval. Value represents in the case of:
     * <ul>
     *     <li>relative date filter - integer value</li>
     *     <li>absolute date filter - date value with format {@code DD-MM-YYYY}</li>
     * </ul>
     *
     * @return date filter from interval value
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns <b>to</b> value of date filter interval. Value represents in the case of:
     * <ul>
     *     <li>relative date filter - integer value</li>
     *     <li>absolute date filter - date value with format {@code YYYY-DD-MM}</li>
     * </ul>
     *
     * @return date filter from interval value
     */
    public String getTo() {
        return to;
    }

    /**
     * @return date interval granularity in the case of relative date filter
     */
    public String getGranularity() {
        return granularity;
    }

    /**
     * @return date dataset URI of this filter
     */
    public String getDataSet() {
        return dataset;
    }

    /**
     * @return date filter type, can be either relative or absolute
     */
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.toString(this);
    }
}
