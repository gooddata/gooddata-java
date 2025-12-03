/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload.processes;

import com.gooddata.sdk.model.dataload.processes.Schedule;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

class ScheduleIdMatcher extends TypeSafeMatcher<Schedule> {
    private final Schedule schedule;

    public ScheduleIdMatcher(final Schedule schedule) {
        this.schedule = schedule;
    }

    public static ScheduleIdMatcher hasSameScheduleIdAs(final Schedule schedule) {
        return new ScheduleIdMatcher(schedule);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Schedule id " + schedule.getId());
    }

    @Override
    protected boolean matchesSafely(Schedule item) {
        return schedule.getId().equals(item.getId());
    }
}