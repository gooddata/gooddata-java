/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Collection;

public class DimensionTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/dimension.json");
        final Dimension dimension = new ObjectMapper().readValue(stream, Dimension.class);
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
}