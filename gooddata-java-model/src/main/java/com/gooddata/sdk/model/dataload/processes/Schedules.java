/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;

/**
 * List of schedules. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("schedules")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = SchedulesDeserializer.class)
public class Schedules extends Page<Schedule> {
    public static final String URI = "/gdc/projects/{projectId}/schedules";

    Schedules(final List<Schedule> items, final Paging paging) {
        super(items, paging);
    }
}
