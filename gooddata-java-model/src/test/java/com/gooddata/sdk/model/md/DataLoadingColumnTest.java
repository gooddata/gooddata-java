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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class DataLoadingColumnTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final DataLoadingColumn column = readObjectFromResource("/md/dataLoadingColumn.json", DataLoadingColumn.class);
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

    @Test
    public void testToStringFormat() throws Exception {
        final DataLoadingColumn column = readObjectFromResource("/md/dataLoadingColumn.json", DataLoadingColumn.class);

        assertThat(column.toString(), matchesPattern(DataLoadingColumn.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final DataLoadingColumn column = readObjectFromResource("/md/dataLoadingColumn.json", DataLoadingColumn.class);
        final DataLoadingColumn deserialized = SerializationUtils.roundtrip(column);

        assertThat(deserialized, jsonEquals(column));
    }
}