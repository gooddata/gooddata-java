package com.gooddata.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BooleanIntegerDeserializerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldDeserializeTrue() throws Exception {
        final String json = MAPPER.writeValueAsString(new BooleanIntegerClass(true));

        final JsonNode node = MAPPER.readTree(json);
        assertThat(node.path("foo").getNumberValue().intValue(), is(1));

        final BooleanIntegerClass moo = MAPPER.readValue(json, BooleanIntegerClass.class);
        assertThat(moo.isFoo(), is(true));
    }

    @Test
    public void shouldDeserializeFalse() throws Exception {
        final String json = MAPPER.writeValueAsString(new BooleanIntegerClass(false));

        final JsonNode node = MAPPER.readTree(json);
        assertThat(node.path("foo").getNumberValue().intValue(), is(0));

        final BooleanIntegerClass moo = MAPPER.readValue(json, BooleanIntegerClass.class);
        assertThat(moo.isFoo(), is(false));
    }
}