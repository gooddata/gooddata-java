/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.sdk.model.executeafm.Qualifier;
import com.gooddata.sdk.model.executeafm.UriObjQualifier;
import com.gooddata.sdk.model.md.visualization.Measure;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Represents measure value filter applied on an insight.
 */
@JsonRootName(MeasureValueFilter.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeasureValueFilter implements ExtendedFilter, CompatibilityFilter, Serializable {

    public static final String NAME = "measureValueFilter";

    private static final long serialVersionUID = -3038654904981929337L;

    private final Qualifier measure;
    private final MeasureValueFilterCondition condition;

    /**
     * Creates a new {@link MeasureValueFilter} instance without specified condition {@link MeasureValueFilterCondition}
     * It represents use-case with not active filter in insight which could be modified later.
     *
     * @param measure The qualifier of referenced measure.
     */
    public MeasureValueFilter(final Qualifier measure) {
        this(measure, null);
    }

    /**
     * Creates a new {@link MeasureValueFilter} instance.
     *
     * @param measure The qualifier of referenced measure.
     * @param condition The condition applied to a sliced measure value. (Optional)
     */
    @JsonCreator
    public MeasureValueFilter(
            @JsonProperty("measure") final Qualifier measure,
            @JsonProperty("condition") final MeasureValueFilterCondition condition) {
        this.measure = notNull(measure, "measure");
        this.condition = condition;
    }

    /**
     * Copy itself using given uri qualifier
     *
     * @param qualifier qualifier to use for the new filter
     *
     * @return self copy with given qualifier
     */
    public MeasureValueFilter withUriObjQualifier(final UriObjQualifier qualifier) {
        return new MeasureValueFilter(qualifier, this.condition);
    }

    /**
     * @return qualifier of referenced measure
     */
    public Qualifier getMeasure() {
        return measure;
    }

    /**
     * @return condition applied to a sliced measure value
     */
    public MeasureValueFilterCondition getCondition() {
        return condition;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MeasureValueFilter that = (MeasureValueFilter) o;
        return Objects.equals(measure, that.measure) &&
                Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(measure, condition);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
