/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collection;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * UsedBy/Using batch result
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UseMany {

    private final Collection<UseManyEntries> useMany;

    @JsonCreator
    private UseMany(@JsonProperty("useMany") final Collection<UseManyEntries> useMany) {
        this.useMany = notNull(useMany, "useMany");
    }

    public Collection<UseManyEntries> getUseMany() {
        return useMany;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
