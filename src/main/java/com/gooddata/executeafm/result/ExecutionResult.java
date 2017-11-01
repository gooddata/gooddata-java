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

    private final Paging paging;

    @JsonProperty("data")
    private List data;
    private List<List<List<AttributeHeaderItem>>> attributeHeaderItems;
    private List<List<List<String>>> totals;

    /**
     * Creates new result
     * @param data result data
     * @param paging result paging
     */
    public ExecutionResult(final String[] data, final Paging paging) {
        this.data = asList(notNull(data, "data"));
        this.paging = notNull(paging, "paging");
    }

    /**
     * Creates new result
     * @param data result data
     * @param paging result paging
     */
    public ExecutionResult(final String[][] data, final Paging paging) {
        this.data = stream(notNull(data, "data")).map(Arrays::asList).collect(toList());
        this.paging = notNull(paging, "paging");
    }

    /**
     * Creates new result
     * @param data result data
     * @param paging result paging
     * @param attributeHeaderItems items for attribute headers, for each attribute in each dimension, there is a list of headers
     * @param totals data of totals, for each total in each dimension, there is a list of total's values
     */
    @JsonCreator
    ExecutionResult(@JsonProperty("data") final List data,
                           @JsonProperty("paging") final Paging paging,
                           @JsonProperty("attributeHeaderItems") final List<List<List<AttributeHeaderItem>>> attributeHeaderItems,
                           @JsonProperty("totals") final List<List<List<String>>> totals) {
        this.data = data;
        this.paging = paging;
        this.attributeHeaderItems = attributeHeaderItems;
        this.totals = totals;
    }

    /**
     * @return result paging
     */
    public Paging getPaging() {
        return paging;
    }

    /**
     * @return attribute header items, for each attribute in each dimension, there is a list of headers
     */
    public List<List<List<AttributeHeaderItem>>> getAttributeHeaderItems() {
        return attributeHeaderItems;
    }

    /**
     * Sets attribute header items, for each attribute in each dimension, there is a list of headers
     * @param attributeHeaderItems header items
     */
    public void setAttributeHeaderItems(final List<List<List<AttributeHeaderItem>>> attributeHeaderItems) {
        this.attributeHeaderItems = attributeHeaderItems;
    }

    /**
     * Add attribute header items for next dimension (this method will add dimension in attribute header items)
     * @param items header items for one dimension
     */
    public void addAttributeHeaderItems(final List<List<AttributeHeaderItem>> items) {
        if (attributeHeaderItems == null) {
            setAttributeHeaderItems(new ArrayList<>());
        }
        attributeHeaderItems.add(items);
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
