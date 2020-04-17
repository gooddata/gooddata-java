/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
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

public class ColumnTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final Column column = readObjectFromResource("/md/column.json", Column.class);
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

    @Test
    public void testToStringFormat() throws Exception {
        final Column column = readObjectFromResource("/md/column.json", Column.class);

        assertThat(column.toString(), matchesPattern(Column.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final Column column = readObjectFromResource("/md/column.json", Column.class);
        final Column deserialized = SerializationUtils.roundtrip(column);

        assertThat(deserialized, jsonEquals(column));
    }
}