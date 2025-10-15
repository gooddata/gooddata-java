/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import com.gooddata.sdk.common.collections.PageDeserializer;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;
import java.util.Map;

class SchedulesDeserializer extends PageDeserializer<Schedules, Schedule> {

    protected SchedulesDeserializer() {
        super(Schedule.class);
    }

    @Override
    protected Schedules createPage(final List<Schedule> items, final Paging paging, final Map<String, String> links) {
        return new Schedules(items, paging);
    }
}

