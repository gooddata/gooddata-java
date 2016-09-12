/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataload.processes;

import com.gooddata.collections.PageableListDeserializer;
import com.gooddata.collections.Paging;

import java.util.List;
import java.util.Map;

class SchedulesDeserializer extends PageableListDeserializer<Schedules, Schedule> {

    protected SchedulesDeserializer() {
        super(Schedule.class);
    }

    @Override
    protected Schedules createList(final List<Schedule> items, final Paging paging, final Map<String, String> links) {
        return new Schedules(items, paging);
    }
}
