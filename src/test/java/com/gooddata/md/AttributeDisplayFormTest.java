/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import org.testng.annotations.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class AttributeDisplayFormTest {

    public static final String FORM_OF = "/gdc/md/PROJECT_ID/obj/DF_FORM_OF_ID";
    public static final String EXPRESSION = "[/gdc/md/PROJECT_ID/obj/DF_EXPRESSION_ID]";
    public static final boolean DEFAULT = false;
    public static final boolean DEFAULT_TRUE = true;
    public static final String LDM_EXPRESSION = "";
    private static final String TYPE = "TYPE";
    private static final String ELEMENTS_LINK = "/gdc/md/PROJECT_ID/obj/DF_ID/elements";

    @SuppressWarnings("deprecation")
    @Test
    public void shouldDeserialize() throws Exception {
        final AttributeDisplayForm attrDF = readObjectFromResource("/md/attributeDisplayForm.json", AttributeDisplayForm.class);
        assertThat(attrDF, is(notNullValue()));

        assertThat(attrDF.getFormOf(), is(FORM_OF));
        assertThat(attrDF.getExpression(), is(EXPRESSION));
        assertThat(attrDF.isDefault(), is(DEFAULT_TRUE));
        assertThat(attrDF.getLdmExpression(), is(LDM_EXPRESSION));
        assertThat(attrDF.getType(), is(TYPE));
        assertThat(attrDF.getElementsLink(), is(ELEMENTS_LINK));
        assertThat(attrDF.getElementsUri(), is(ELEMENTS_LINK));
    }

    @Test
    public void testSerialization() throws Exception {
        final DisplayForm attrDF = new AttributeDisplayForm("Person Name", FORM_OF, EXPRESSION, DEFAULT,
                LDM_EXPRESSION, TYPE, ELEMENTS_LINK);

        assertThat(attrDF, serializesToJson("/md/attributeDisplayForm-input.json"));
    }

    @Test
    public void shouldSerializeSameAsDeserializationInput() throws Exception {
        final AttributeDisplayForm attrDF = readObjectFromResource("/md/attributeDisplayForm.json", AttributeDisplayForm.class);
        assertThat(attrDF, serializesToJson("/md/attributeDisplayForm-inputOrig.json"));
    }

    @Test
    public void testToStringFormat() {
        final DisplayForm attrDF = new AttributeDisplayForm("Person Name", FORM_OF, EXPRESSION, DEFAULT,
                LDM_EXPRESSION, TYPE, ELEMENTS_LINK);

        assertThat(attrDF.toString(), matchesPattern(AttributeDisplayForm.class.getSimpleName() + "\\[.*\\]"));
    }

}
