/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

public class SchedulesTest {

    @Test
    public void testDeserialization() throws Exception {
        final Schedules schedules = readObjectFromResource("/dataload/processes/schedules.json", Schedules.class);

        assertThat(schedules, notNullValue());
        final List<Schedule> pageItems = schedules.getPageItems();
        assertThat(pageItems, hasSize(1));
        assertThat(pageItems.get(0).getId(), is("schedule_id"));
        assertThat(pageItems.get(0).getType(), is("MSETL"));
        assertThat(schedules.getNextPage(), nullValue());
    }

    @Test
    public void testDeserializationPaging() throws Exception {
        final Schedules schedules = readObjectFromResource("/dataload/processes/schedules_page1.json", Schedules.class);

        assertThat(schedules, notNullValue());
        final List<Schedule> pageItems = schedules.getPageItems();
        assertThat(pageItems, hasSize(1));
        assertThat(pageItems.get(0).getId(), is("schedule_id_1"));
        assertThat(pageItems.get(0).getType(), is("MSETL"));
        assertThat(schedules.getNextPage(), notNullValue());
        assertThat(schedules.getNextPage().getPageUri(null).toString(), is("/gdc/projects/PROJECT_ID/schedules?offset=1&limit=1"));
    }
}