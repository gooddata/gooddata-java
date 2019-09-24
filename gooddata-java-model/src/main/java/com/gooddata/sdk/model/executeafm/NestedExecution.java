/*
 * Copyright (C) 2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.model.executeafm.afm.Afm;
import com.gooddata.sdk.model.executeafm.resultspec.ResultSpec;
import com.gooddata.util.GoodDataToStringBuilder;

/**
 * Execution representation which could be nested in other json objects definitions without json wrapper object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NestedExecution {
    private final Afm afm;
    private ResultSpec resultSpec;

    @JsonCreator
    public NestedExecution(@JsonProperty("afm") Afm afm, @JsonProperty("resultSpec") ResultSpec resultSpec) {
        this.afm = afm;
        this.resultSpec = resultSpec;
    }

    public NestedExecution(@JsonProperty("afm") Afm afm) {
        this.afm = afm;
    }

    public Afm getAfm() {
        return this.afm;
    }

    public ResultSpec getResultSpec() {
        return this.resultSpec;
    }

    public void setResultSpec(ResultSpec resultSpec) {
        this.resultSpec = resultSpec;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
