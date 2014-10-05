/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DisplayFormTest {

    public static final String FORM_OF = "/gdc/md/PROJECT_ID/obj/DF_FORM_OF_ID";
    public static final String EXPRESSION = "[/gdc/md/PROJECT_ID/obj/DF_EXPRESSION_ID]";
    public static final Integer DEFAULT = 0;
    public static final String LDM_EXPRESSION = "";

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/displayForm.json");
        final DisplayForm displayForm = new ObjectMapper().readValue(stream, DisplayForm.class);
        assertThat(displayForm, is(notNullValue()));

        assertThat(displayForm.getFormOf(), is(FORM_OF));
        assertThat(displayForm.getExpression(), is(EXPRESSION));
        assertThat(displayForm.getDefault(), is(DEFAULT));
        assertThat(displayForm.getLdmExpression(), is(LDM_EXPRESSION));
    }

    @Test
    public void testSerialization() throws Exception {
        final DisplayForm displayForm = new DisplayForm(new Meta("Person Name"),
                new DisplayForm.Content(FORM_OF, EXPRESSION, DEFAULT, LDM_EXPRESSION));

        assertThat(displayForm, serializesToJson("/md/displayForm-input.json"));
    }

}
