/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ExpressionTest {

    public static final String DATA = "/gdc/md/PROJECT_ID/obj/EXPR_ID";
    public static final String TYPE = "expr";
    public static final String JSON = "{\"data\":\"/gdc/md/PROJECT_ID/obj/EXPR_ID\",\"type\":\"expr\"}";

    @Test
    public void testDeserialize() throws Exception {
        final Expression expression = OBJECT_MAPPER.readValue(JSON, Expression.class);
        assertThat(expression, is(notNullValue()));
        assertThat(expression.getData(), is(DATA));
        assertThat(expression.getType(), is(TYPE));
    }

    @Test
    public void testSerialize() throws Exception {
        final Expression expression = new Expression(DATA, TYPE);
        assertThat(OBJECT_MAPPER.writeValueAsString(expression), is(JSON));
    }

    @Test
    public void testToStringFormat() {
        final Expression expression = new Expression(DATA, TYPE);

        assertThat(expression.toString(), matchesPattern(Expression.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final Expression expression = new Expression(DATA, TYPE);
        final Expression deserialized = SerializationUtils.roundtrip(expression);

        assertThat(deserialized, jsonEquals(expression));
    }
}