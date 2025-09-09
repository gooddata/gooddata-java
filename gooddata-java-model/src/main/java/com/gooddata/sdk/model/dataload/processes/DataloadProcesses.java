/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collection;
import java.util.List;

/**
 * List of dataload processes. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("processes")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataloadProcesses {
    public static final String URI = "/gdc/projects/{projectId}/dataload/processes";

    public static final String USER_PROCESSES_URI = Account.URI + "/dataload/processes";

    private final List<DataloadProcess> items;

    @JsonCreator
    DataloadProcesses(@JsonProperty("items") List<DataloadProcess> items) {
        this.items = items;
    }

    public Collection<DataloadProcess> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}

