/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BooleanIntegerSerializerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldSerializeTrue() throws Exception {
        final String json = MAPPER.writeValueAsString(new BooleanIntegerClass(true));

        final JsonNode node = MAPPER.readTree(json);
        assertThat(node.path("foo").numberValue().intValue(), is(1));
    }

    @Test
    public void shouldSerializeFalse() throws Exception {
        final String json = MAPPER.writeValueAsString(new BooleanIntegerClass(false));

        final JsonNode node = MAPPER.readTree(json);
        assertThat(node.path("foo").numberValue().intValue(), is(0));
    }
}