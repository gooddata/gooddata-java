package com.gooddata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GDDateSerializerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testSerialize() throws Exception {
        final GDDateClass foo = new GDDateClass(new LocalDate(2012, 3, 20));
        final String json = MAPPER.writeValueAsString(foo);
        final JsonNode node = MAPPER.readTree(json);

        assertThat(node.path("date").textValue(), is("2012-03-20"));
    }
}
