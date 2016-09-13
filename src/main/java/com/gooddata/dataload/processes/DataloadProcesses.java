/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload.processes;

import com.gooddata.account.Account;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.web.util.UriTemplate;

import java.util.Collection;
import java.util.List;

/**
 * List of dataload processes. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("processes")
@JsonIgnoreProperties(ignoreUnknown = true)
class DataloadProcesses {
    public static final String URI = "/gdc/projects/{projectId}/dataload/processes";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    public static final String USER_PROCESSES_URI = Account.URI + "/dataload/processes";
    public static final UriTemplate USER_PROCESSES_TEMPLATE = new UriTemplate(USER_PROCESSES_URI);

    private final List<DataloadProcess> items;

    @JsonCreator
    DataloadProcesses(@JsonProperty("items") List<DataloadProcess> items) {
        this.items = items;
    }

    Collection<DataloadProcess> getItems() {
        return items;
    }
}
