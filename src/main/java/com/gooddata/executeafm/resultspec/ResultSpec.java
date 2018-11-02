/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.resultspec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.executeafm.afm.Afm;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.gooddata.util.Validate.notNull;

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

    public List<SortItem> getSorts() {
        return sorts;
    }

    public void setDimensions(final List<Dimension> dimensions) {
        this.dimensions = dimensions;
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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ResultSpec that = (ResultSpec) o;
        return Objects.equals(dimensions, that.dimensions) &&
                Objects.equals(sorts, that.sorts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimensions, sorts);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
