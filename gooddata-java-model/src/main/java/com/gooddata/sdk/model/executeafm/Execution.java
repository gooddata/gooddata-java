/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
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

/**
 * Represents structure for triggering execution of contained AFM (Attributes Filters Metrics).
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("execution")
public class Execution extends NestedExecution {
    @JsonCreator
    public Execution(@JsonProperty("afm") final Afm afm,
                     @JsonProperty("resultSpec") final ResultSpec resultSpec) {
        super(afm, resultSpec);
    }

    public Execution(final Afm afm) {
        super(afm);
    }
}
