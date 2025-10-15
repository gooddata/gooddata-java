/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class TableDataLoadTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final TableDataLoad load = readObjectFromResource("/md/tableDataLoad.json", TableDataLoad.class);
        assertThat(load, is(notNullValue()));

        assertThat(load.getDataSourceLocation(), is("d_zendesktickets_vehicleview_aaavxfdfgqamowg"));
        assertThat(load.getType(), is("incremental"));
        assertThat(load.isFull(), is(false));
        assertThat(load.isIncremental(), is(true));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final TableDataLoad load = readObjectFromResource("/md/tableDataLoad.json", TableDataLoad.class);

        assertThat(load.toString(), matchesPattern(TableDataLoad.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final TableDataLoad load = readObjectFromResource("/md/tableDataLoad.json", TableDataLoad.class);
        final TableDataLoad deserialized = SerializationUtils.roundtrip(load);

        assertThat(deserialized, jsonEquals(load));
    }

}
