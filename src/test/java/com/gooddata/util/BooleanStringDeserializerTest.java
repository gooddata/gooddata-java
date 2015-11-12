package com.gooddata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BooleanStringDeserializerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldDeserializeTrue() throws Exception {
        final String json = MAPPER.writeValueAsString(new BooleanStringClass(true));

        final JsonNode node = MAPPER.readTree(json);
        assertThat(node.path("foo").textValue(), is("1"));

        final BooleanStringClass moo = MAPPER.readValue(json, BooleanStringClass.class);
        assertThat(moo.isFoo(), is(true));
    }

    @Test
    public void shouldDeserializeFalse() throws Exception {
        final String json = MAPPER.writeValueAsString(new BooleanStringClass(false));

        final JsonNode node = MAPPER.readTree(json);
        assertThat(node.path("foo").textValue(), is("0"));

        final BooleanStringClass moo = MAPPER.readValue(json, BooleanStringClass.class);
        assertThat(moo.isFoo(), is(false));
    }

    @Test(expectedExceptions = JsonMappingException.class)
    public void shouldThrowOnDeserializingInt() throws Exception {
        MAPPER.readValue("{\"foo\":0}", BooleanStringClass.class);
    }
}