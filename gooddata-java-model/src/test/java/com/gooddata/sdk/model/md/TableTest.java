/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class TableTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final Table table = readObjectFromResource("/md/table.json", Table.class);
        assertThat(table, is(notNullValue()));

        assertThat(table.getDBName(), is("d_zendesktickets_vehicleview"));
        assertThat(table.getActiveDataLoad(), is("/gdc/md/PROJECT_ID/obj/625412"));
        assertThat(table.getDataLoads(), hasItems(
                                            "/gdc/md/PROJECT_ID/obj/625283",
                                            "/gdc/md/PROJECT_ID/obj/625412"));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final Table table = readObjectFromResource("/md/table.json", Table.class);

        assertThat(table.toString(), matchesPattern(Table.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final Table table = readObjectFromResource("/md/table.json", Table.class);
        final Table deserialized = SerializationUtils.roundtrip(table);

        assertThat(deserialized, jsonEquals(table));
    }
}