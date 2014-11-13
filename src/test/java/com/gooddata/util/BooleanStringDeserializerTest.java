package com.gooddata.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BooleanStringDeserializerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldDeserializeTrue() throws Exception {
        final String json = MAPPER.writeValueAsString(new BooleanStringClass(true));

        final JsonNode node = MAPPER.readTree(json);
        assertThat(node.path("foo").getTextValue(), is("1"));

        final BooleanStringClass moo = MAPPER.readValue(json, BooleanStringClass.class);
        assertThat(moo.isFoo(), is(true));
    }

    @Test
    public void shouldDeserializeFalse() throws Exception {
        final String json = MAPPER.writeValueAsString(new BooleanStringClass(false));

        final JsonNode node = MAPPER.readTree(json);
        assertThat(node.path("foo").getTextValue(), is("0"));

        final BooleanStringClass moo = MAPPER.readValue(json, BooleanStringClass.class);
        assertThat(moo.isFoo(), is(false));
    }
}