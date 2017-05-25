/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDate;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.OBJECT_MAPPER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GDDateDeserializerTest {

    @Test
    public void testDeserialize() throws Exception {
        final String json = OBJECT_MAPPER.writeValueAsString(new GDDateClass(new LocalDate(2012, 3, 20)));

        final JsonNode node = OBJECT_MAPPER.readTree(json);
        assertThat(node.path("date").textValue(), is("2012-03-20"));

        final GDDateClass date = OBJECT_MAPPER.readValue(json, GDDateClass.class);
        assertThat(date.getDate(), is(new LocalDate(2012, 3, 20)));
    }


}
