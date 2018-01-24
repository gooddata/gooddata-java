/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class DisplayFormTest {

    public static final String FORM_OF = "/gdc/md/PROJECT_ID/obj/DF_FORM_OF_ID";
    public static final String EXPRESSION = "[/gdc/md/PROJECT_ID/obj/DF_EXPRESSION_ID]";
    public static final String LDM_EXPRESSION = "";
    private static final String ELEMENTS_LINK = "/gdc/md/PROJECT_ID/obj/DF_ID/elements";

    @SuppressWarnings("deprecation")
    @Test
    public void shouldDeserialize() throws Exception {
        final DisplayForm displayForm = readObjectFromResource("/md/displayForm.json", DisplayForm.class);
        assertThat(displayForm, is(notNullValue()));

        assertThat(displayForm.getFormOf(), is(FORM_OF));
        assertThat(displayForm.getExpression(), is(EXPRESSION));
        assertThat(displayForm.getLdmExpression(), is(LDM_EXPRESSION));
        assertThat(displayForm.getType(), is(nullValue()));
        assertThat(displayForm.getElementsLink(), is(ELEMENTS_LINK));
        assertThat(displayForm.getElementsUri(), is(ELEMENTS_LINK));
    }

    @Test
    public void testSerialization() throws Exception {
        final DisplayForm displayForm = new DisplayForm(new Meta("Person Name"),
                new DisplayForm.Content(FORM_OF, EXPRESSION,  LDM_EXPRESSION, null), new DisplayForm.Links(ELEMENTS_LINK));

        assertThat(displayForm, jsonEquals(resource("md/displayForm-input.json")));
    }

    @Test
    public void testToStringFormat() {
        final DisplayForm displayForm = new DisplayForm(new Meta("Person Name"),
                new DisplayForm.Content(FORM_OF, EXPRESSION,  LDM_EXPRESSION, null), new DisplayForm.Links(ELEMENTS_LINK));

        assertThat(displayForm.toString(), matchesPattern(DisplayForm.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final DisplayForm displayForm = new DisplayForm(new Meta("Person Name"),
                new DisplayForm.Content(FORM_OF, EXPRESSION,  LDM_EXPRESSION, null), new DisplayForm.Links(ELEMENTS_LINK));
        final DisplayForm deserialized = SerializationUtils.roundtrip(displayForm);

        assertThat(deserialized, jsonEquals(displayForm));
    }
}
