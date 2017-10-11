/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.executeafm.afm.ObjectAfm;
import com.gooddata.executeafm.resultspec.ResultSpec;
import com.gooddata.util.GoodDataToStringBuilder;

/**
 * Represents structure for triggering execution of contained AFM (Attributes Filters Metrics).
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("execution")
public class Execution {

    private final ObjectAfm afm;
    private ResultSpec resultSpec;

    @JsonCreator
    public Execution(@JsonProperty("afm") final ObjectAfm afm,
                     @JsonProperty("resultSpec") final ResultSpec resultSpec) {
        this.afm = afm;
        this.resultSpec = resultSpec;
    }

    public Execution(final ObjectAfm afm) {
        this.afm = afm;
    }

    public ObjectAfm getAfm() {
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
