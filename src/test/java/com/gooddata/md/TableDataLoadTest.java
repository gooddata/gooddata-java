/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

public class TableDataLoadTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/tableDataLoad.json");
        final TableDataLoad load = new ObjectMapper().readValue(stream, TableDataLoad.class);
        assertThat(load, is(notNullValue()));

        assertThat(load.getDataSourceLocation(), is("d_zendesktickets_vehicleview_aaavxfdfgqamowg"));
        assertThat(load.getType(), is("incremental"));
        assertThat(load.isFull(), is(false));
        assertThat(load.isIncremental(), is(true));
    }

}