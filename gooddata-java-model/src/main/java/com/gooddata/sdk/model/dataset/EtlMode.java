/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

@JsonTypeName("etlMode")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EtlMode {

    public static final String URL = "/gdc/md/{project}/etl/mode";

    private final EtlModeType mode;

    private final LookupMode lookup;

    @JsonCreator
    public EtlMode(@JsonProperty("mode") final EtlModeType mode,
                   @JsonProperty("lookup") final LookupMode lookup) {
        this.mode = mode;
        this.lookup = lookup;
    }

    public EtlModeType getMode() {
        return mode;
    }

    public LookupMode getLookup() {
        return lookup;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
