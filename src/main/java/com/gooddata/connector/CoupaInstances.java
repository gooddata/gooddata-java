/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

/**
 * Internal only. Deserialization only.
 * Contains collection of coupa instances.
 */
@JsonTypeName("coupaInstances")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
class CoupaInstances {

    static final String URL = "/gdc/projects/{project}/connectors/coupa/integration/config/settings/instances";

    private final Collection<CoupaInstance> items;

    @JsonCreator
    private CoupaInstances(@JsonProperty("items") List<CoupaInstance> items) {
        this.items = items;
    }

    Collection<CoupaInstance> getItems() {
        return items;
    }
}
