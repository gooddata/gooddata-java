/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.resultspec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import static com.gooddata.util.Validate.notNull;

/**
 * Define sort by specific attribute
 *
 * <p>With "aggregation" active you can sort all elements of attribute
 * by "aggregation fn" applied to all valid values belonging to each
 * element. This is extremely useful when sorting stacked
 * visualizations like stack bar/area charts. Currently supported is
 * only "sum", see {@link AttributeSortAggregation}</p>
 *
 * <p>Simple example (dimension = Year, measureGroup; 2 metrics; sort
 * on Year with aggregation="sum", descending):</p>
 * <pre>
 * Year       2006        2007
 * Names     M1  M2      M1   M2
 * Values    1    2       3    4
 * </pre>
 *
 * <p>We take all values belonging to each attribute element of chosen attribute
 * and apply selected function (sum) on them. Notice that we are summarising
 * values from different metrics:</p>
 * <pre>
 * 2006 (1 + 2 = 3)
 * 2007 (3 + 4 = 7)
 * </pre>
 *
 * <p>After that we shuffle year attribute elements related to results from "sum"
 * function:</p>
 * <pre>
 * Year       2007        2006
 * Names     M1  M2      M1   M2
 * Values    3    4       1    2
 * </pre>
 */
@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.NAME)
@JsonTypeName("attributeSortItem")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeSortItem implements SortItem {

    private final String direction;
    private final String attributeIdentifier;
    private final String aggregation;

    @JsonCreator
    public AttributeSortItem(@JsonProperty("direction") final String direction,
                             @JsonProperty("attributeIdentifier") final String attributeIdentifier,
                             @JsonProperty("aggregation") final String aggregation) {
        this.attributeIdentifier = attributeIdentifier;
        this.direction = direction;
        this.aggregation = aggregation;
    }

    public AttributeSortItem(final String direction,
                             final String attributeIdentifier) {
        this(direction, attributeIdentifier, null);
    }

    public AttributeSortItem(final Direction direction, final String attributeIdentifier) {
        this(notNull(direction, "direction").toString(), attributeIdentifier, null);
    }

    public AttributeSortItem(final Direction direction, final String attributeIdentifier, final AttributeSortAggregation aggregation) {
        this(notNull(direction, "direction").toString(), attributeIdentifier, notNull(aggregation, "aggregation").toString());
    }

    public String getDirection() {
        return direction;
    }

    public String getAttributeIdentifier() {
        return attributeIdentifier;
    }

    public String getAggregation() {
        return aggregation;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
