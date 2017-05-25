/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import org.testng.annotations.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
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
        assertThat(fact, serializesToJson("/md/fact-input.json"));
    }

    @Test
    public void testToStringFormat() {
        final Fact fact = new Fact("Person Shoe Size", "/gdc/md/PROJECT_ID/obj/COL_ID", "col", "/gdc/md/PROJECT_ID/obj/FOLDER_ID");

        assertThat(fact.toString(), matchesPattern(Fact.class.getSimpleName() + "\\[.*\\]"));
    }

}