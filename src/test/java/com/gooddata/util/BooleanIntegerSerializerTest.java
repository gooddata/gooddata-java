package com.gooddata.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BooleanIntegerSerializerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldSerializeTrue() throws Exception {
        final String json = MAPPER.writeValueAsString(new BooleanIntegerClass(true));

        final JsonNode node = MAPPER.readTree(json);
        assertThat(node.path("foo").getNumberValue().intValue(), is(1));
    }

    @Test
    public void shouldSerializeFalse() throws Exception {
        final String json = MAPPER.writeValueAsString(new BooleanIntegerClass(false));

        final JsonNode node = MAPPER.readTree(json);
        assertThat(node.path("foo").getNumberValue().intValue(), is(0));
    }
}