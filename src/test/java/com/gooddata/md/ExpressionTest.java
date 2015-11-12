/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExpressionTest {

    public static final String DATA = "/gdc/md/PROJECT_ID/obj/EXPR_ID";
    public static final String TYPE = "expr";
    public static final String JSON = "{\"data\":\"/gdc/md/PROJECT_ID/obj/EXPR_ID\",\"type\":\"expr\"}";
    public static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testDeserialize() throws Exception {
        final Expression expression = MAPPER.readValue(JSON, Expression.class);
        assertThat(expression, is(notNullValue()));
        assertThat(expression.getData(), is(DATA));
        assertThat(expression.getType(), is(TYPE));
    }

    @Test
    public void testSerialize() throws Exception {
        final Expression expression = new Expression(DATA, TYPE);
        assertThat(MAPPER.writeValueAsString(expression), is(JSON));
    }
}