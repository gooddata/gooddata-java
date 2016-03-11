/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

public class DataLoadingColumnTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/dataLoadingColumn.json");
        final DataLoadingColumn column = new ObjectMapper().readValue(stream, DataLoadingColumn.class);
        assertThat(column, is(notNullValue()));

        assertThat(column.getColumnUri(), is("/gdc/md/PROJECT_ID/obj/COLUMN_ID"));

        assertThat(column.getType(), is("INT"));
        assertThat(column.hasTypeInt(), is(true));
        assertThat(column.hasTypeVarchar(), is(false));

        assertThat(column.getName(), is("COLUMN_NAME"));
        assertThat(column.getLength(), is(128));
        assertThat(column.getPrecision(), is(nullValue()));
        assertThat(column.isUnique(), is(false));
        assertThat(column.isNullable(), is(true));

        assertThat(column.getSynchronizeType(), is("INT"));
        assertThat(column.getSynchronizeLength(), is(128));
        assertThat(column.getSynchronizePrecision(), is(0));
    }


}