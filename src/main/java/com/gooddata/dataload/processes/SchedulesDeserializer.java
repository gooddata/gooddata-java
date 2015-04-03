package com.gooddata.dataload.processes;

import com.gooddata.collections.PageableListDeserializer;
import com.gooddata.collections.Paging;

import java.util.List;

class SchedulesDeserializer extends PageableListDeserializer<Schedules, Schedule> {

    protected SchedulesDeserializer() {
        super(Schedule.class);
    }

    @Override
    protected Schedules createList(final List<Schedule> items, final Paging paging) {
        return new Schedules(items, paging);
    }
}
