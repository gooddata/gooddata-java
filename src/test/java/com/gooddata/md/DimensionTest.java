/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import org.testng.annotations.Test;

import java.util.Collection;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class DimensionTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final Dimension dimension = readObjectFromResource("/md/dimension.json", Dimension.class);
        assertThat(dimension, is(notNullValue()));

        final Collection<NestedAttribute> attributes = dimension.getAttributes();
        assertThat(attributes, is(notNullValue()));
        assertThat(attributes, hasSize(2));
    }

    @Test
    public void shouldSerialize() throws Exception {
        final Dimension dimension = new Dimension("Dimension");
        assertThat(dimension, serializesToJson("/md/dimension-input.json"));

    }

    @Test
    public void testToStringFormat() {
        final Dimension dimension = new Dimension("Dimension");

        assertThat(dimension.toString(), matchesPattern(Dimension.class.getSimpleName() + "\\[.*\\]"));
    }
}