/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.model.executeafm.afm.Afm;
import com.gooddata.sdk.model.executeafm.resultspec.ResultSpec;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

/**
 * Represents structure for triggering execution of contained AFM (Attributes Filters Metrics).
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("execution")
public class Execution {

    private final Afm afm;
    private ResultSpec resultSpec;

    @JsonCreator
    public Execution(@JsonProperty("afm") final Afm afm,
                     @JsonProperty("resultSpec") final ResultSpec resultSpec) {
        this.afm = afm;
        this.resultSpec = resultSpec;
    }

    public Execution(final Afm afm) {
        this.afm = afm;
    }

    public Afm getAfm() {
        return afm;
    }

    public ResultSpec getResultSpec() {
        return resultSpec;
    }

    public void setResultSpec(final ResultSpec resultSpec) {
        this.resultSpec = resultSpec;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}

