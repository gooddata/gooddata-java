/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class DataloadProcessesTest {

    @Test
    public void testDeserialization() throws Exception {
        final DataloadProcesses processes = readObjectFromResource("/dataload/processes/processes.json", DataloadProcesses.class);

        assertThat(processes, is(notNullValue()));
        assertThat(processes.getItems(), hasSize(1));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final DataloadProcesses processes = readObjectFromResource("/dataload/processes/processes.json", DataloadProcesses.class);

        assertThat(processes.toString(), matchesPattern(DataloadProcesses.class.getSimpleName() + "\\[.*\\]"));
    }

}