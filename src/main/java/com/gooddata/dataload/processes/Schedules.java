/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload.processes;

import com.gooddata.collections.PageableList;
import com.gooddata.collections.Paging;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.web.util.UriTemplate;

import java.util.List;

/**
 * List of schedules. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("schedules")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = SchedulesDeserializer.class)
class Schedules extends PageableList<Schedule> {
    public static final String URI = "/gdc/projects/{projectId}/schedules";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    Schedules(final List<Schedule> items, final Paging paging) {
        super(items, paging);
    }
}
