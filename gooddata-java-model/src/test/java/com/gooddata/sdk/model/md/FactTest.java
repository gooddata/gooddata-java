/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class FactTest {

    @Test
    public void testDeserialization() throws Exception {
        final Fact fact = readObjectFromResource("/md/fact.json", Fact.class);
        assertThat(fact, is(notNullValue()));
        assertThat(fact.getExpressions(), is(notNullValue()));
        assertThat(fact.getExpressions(), hasSize(1));
        assertThat(fact.getFolders(), is(notNullValue()));
        assertThat(fact.getFolders(), hasSize(2));
    }

    @Test
    public void testSerialization() throws Exception {
        final Fact fact = new Fact("Person Shoe Size", "/gdc/md/PROJECT_ID/obj/COL_ID", "col", "/gdc/md/PROJECT_ID/obj/FOLDER_ID");
        assertThat(fact, jsonEquals(resource("md/fact-input.json")));
    }

    @Test
    public void testToStringFormat() {
        final Fact fact = new Fact("Person Shoe Size", "/gdc/md/PROJECT_ID/obj/COL_ID", "col", "/gdc/md/PROJECT_ID/obj/FOLDER_ID");

        assertThat(fact.toString(), matchesPattern(Fact.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final Fact fact = new Fact("Person Shoe Size", "/gdc/md/PROJECT_ID/obj/COL_ID", "col", "/gdc/md/PROJECT_ID/obj/FOLDER_ID");
        final Fact deserialized = SerializationUtils.roundtrip(fact);

        assertThat(deserialized, jsonEquals(fact));
    }
}