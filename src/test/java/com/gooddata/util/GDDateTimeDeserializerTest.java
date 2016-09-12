/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GDDateTimeDeserializerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testDeserialize() throws Exception {
        final String json = MAPPER.writeValueAsString(new GDDateTimeClass(new DateTime(2012, 3, 20, 14, 31, 5, 3, DateTimeZone.UTC)));

        final JsonNode node = MAPPER.readTree(json);
        assertThat(node.path("date").textValue(), is("2012-03-20 14:31:05"));

        final GDDateTimeClass date = MAPPER.readValue(json, GDDateTimeClass.class);
        assertThat(date.getDate(), is(new DateTime(2012, 3, 20, 14, 31, 5, DateTimeZone.UTC)));
    }
}
