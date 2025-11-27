/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.resultspec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import com.gooddata.sdk.model.executeafm.afm.Afm;

import java.util.ArrayList;
import java.util.List;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Represents the result specification of executed {@link Afm}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultSpec {
    private List<Dimension> dimensions;
    private List<SortItem> sorts;

    @JsonCreator
    public ResultSpec(
            @JsonProperty("dimensions") final List<Dimension> dimensions,
            @JsonProperty("sorts") final List<SortItem> sorts) {
        this.dimensions = dimensions;
        this.sorts = sorts;
    }

    public ResultSpec() {
    }

    public List<Dimension> getDimensions() {
        return dimensions;
    }

    public void setDimensions(final List<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    public List<SortItem> getSorts() {
        return sorts;
    }

    public void setSorts(final List<SortItem> sorts) {
        this.sorts = sorts;
    }

    public ResultSpec addDimension(final Dimension dimension) {
        if (dimensions == null) {
            setDimensions(new ArrayList<>());
        }
        dimensions.add(notNull(dimension, "dimension"));
        return this;
    }

    public ResultSpec addSort(final SortItem sort) {
        if (sorts == null) {
            setSorts(new ArrayList<>());
        }
        sorts.add(notNull(sort, "sort"));
        return this;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
