/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BooleanStringSerializerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldSerializeTrue() throws Exception {
        final String json = MAPPER.writeValueAsString(new BooleanStringClass(true));

        final JsonNode node = MAPPER.readTree(json);
        assertThat(node.path("foo").textValue(), is("1"));
    }

    @Test
    public void shouldSerializeFalse() throws Exception {
        final String json = MAPPER.writeValueAsString(new BooleanStringClass(false));

        final JsonNode node = MAPPER.readTree(json);
        assertThat(node.path("foo").textValue(), is("0"));
    }
}