/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

public class TableTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/table.json");
        final Table table = new ObjectMapper().readValue(stream, Table.class);
        assertThat(table, is(notNullValue()));

        assertThat(table.getDBName(), is("d_zendesktickets_vehicleview"));
        assertThat(table.getActiveDataLoad(), is("/gdc/md/PROJECT_ID/obj/625412"));
        assertThat(table.getDataLoads(), hasItems(
                                            "/gdc/md/PROJECT_ID/obj/625283",
                                            "/gdc/md/PROJECT_ID/obj/625412"));
    }

}