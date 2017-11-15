/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gooddata.util.Validate.notNull;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ArrayUtils.toObject;

/**
 * Data result of the {@link com.gooddata.executeafm.Execution}.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("executionResult")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExecutionResult {

    private final DataList data;
    private final Paging paging;

    private List<List<List<ResultHeaderItem>>> headerItems;
    private List<List<List<String>>> totals;

    /**
     * Creates new result
     * @param data result data
     * @param paging result paging
     */
    public ExecutionResult(final String[] data, final Paging paging) {
        this.data = new DataList(notNull(data, "data"));
        this.paging = notNull(paging, "paging");
    }

    /**
     * Creates new result
     * @param data result data
     * @param paging result paging
     */
    public ExecutionResult(final String[][] data, final Paging paging) {
        this.data = new DataList(notNull(data, "data"));
        this.paging = notNull(paging, "paging");
    }

    /**
     * Creates new result
     * @param data result data
     * @param paging result paging
     * @param headerItems items for headers, for each header in each dimension, there is a list of header items
     * @param totals data of totals, for each total in each dimension, there is a list of total's values
     */
    @JsonCreator
    ExecutionResult(@JsonProperty("data") final DataList data,
                           @JsonProperty("paging") final Paging paging,
                           @JsonProperty("headerItems") final List<List<List<ResultHeaderItem>>> headerItems,
                           @JsonProperty("totals") final List<List<List<String>>> totals) {
        this.data = data;
        this.paging = paging;
        this.headerItems = headerItems;
        this.totals = totals;
    }

    /**
     * @return result data
     */
    public DataList getData() {
        return data;
    }

    /**
     * @return result paging
     */
    public Paging getPaging() {
        return paging;
    }

    /**
     * @return header items, for each header in each dimension, there is a list of header items
     */
    public List<List<List<ResultHeaderItem>>> getHeaderItems() {
        return headerItems;
    }

    /**
     * Sets header items, for each header in each dimension, there is a list of header items
     * @param headerItems header items
     */
    public void setHeaderItems(final List<List<List<ResultHeaderItem>>> headerItems) {
        this.headerItems = headerItems;
    }

    /**
     * Add header items for next dimension (this method will add dimension in header items)
     * @param items header items for one dimension
     */
    public void addHeaderItems(final List<List<ResultHeaderItem>> items) {
        if (headerItems == null) {
            setHeaderItems(new ArrayList<>());
        }
        headerItems.add(items);
    }

    /**
     * @return data of totals, for each total in each dimension, there is a list of total's values
     */
    public List<List<List<String>>> getTotals() {
        return totals;
    }

    /**
     * Sets total data, for each total in each dimension, there is a list of total's values
     * @param totals totals data
     */
    public void setTotals(final List<List<List<String>>> totals) {
        this.totals = totals;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
