/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataload.processes;

import com.gooddata.collections.PageableList;
import com.gooddata.collections.Paging;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
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
