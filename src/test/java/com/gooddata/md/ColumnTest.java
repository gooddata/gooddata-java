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

public class ColumnTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/column.json");
        final Column column = new ObjectMapper().readValue(stream, Column.class);
        assertThat(column, is(notNullValue()));

        assertThat(column.getTableUri(), is("/gdc/md/PROJECT_ID/obj/9538"));
        assertThat(column.getType(), is("pk"));
        assertThat(column.getDBName(), is("id"));
        assertThat(column.isPk(), is(true));
        assertThat(column.isInputPk(), is(false));
        assertThat(column.isFk(), is(false));
        assertThat(column.isFact(), is(false));
        assertThat(column.isDisplayForm(), is(false));
    }

}