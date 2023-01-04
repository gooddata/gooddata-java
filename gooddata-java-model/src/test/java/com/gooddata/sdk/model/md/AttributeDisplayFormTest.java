/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class AttributeDisplayFormTest {

    private static final String FORM_OF = "/gdc/md/PROJECT_ID/obj/DF_FORM_OF_ID";
    private static final String EXPRESSION = "[/gdc/md/PROJECT_ID/obj/DF_EXPRESSION_ID]";
    private static final boolean DEFAULT = false;
    private static final boolean DEFAULT_TRUE = true;
    private static final String LDM_EXPRESSION = "";
    private static final String TYPE = "TYPE";
    private static final String ELEMENTS_LINK = "/gdc/md/PROJECT_ID/obj/DF_ID/elements";
    private static final String TITLE = "Person Name";

    @Test
    public void shouldDeserialize() throws Exception {
        final AttributeDisplayForm attrDF = readObjectFromResource("/md/attributeDisplayForm.json", AttributeDisplayForm.class);
        assertThat(attrDF, is(notNullValue()));

        assertThat(attrDF.getFormOf(), is(FORM_OF));
        assertThat(attrDF.getExpression(), is(EXPRESSION));
        assertThat(attrDF.isDefault(), is(DEFAULT_TRUE));
        assertThat(attrDF.getLdmExpression(), is(LDM_EXPRESSION));
        assertThat(attrDF.getType(), is(TYPE));
        assertThat(attrDF.getElementsUri(), is(ELEMENTS_LINK));
    }

    @Test
    public void testSerialization() throws Exception {
        final DisplayForm attrDF = new AttributeDisplayForm(TITLE, FORM_OF, EXPRESSION, DEFAULT,
                LDM_EXPRESSION, TYPE, ELEMENTS_LINK);

        assertThat(attrDF, jsonEquals(resource("md/attributeDisplayForm-input.json")));
    }

    @Test
    public void shouldSerializeSameAsDeserializationInput() {
        final AttributeDisplayForm attrDF = readObjectFromResource("/md/attributeDisplayForm.json", AttributeDisplayForm.class);
        assertThat(attrDF, jsonEquals(resource("md/attributeDisplayForm-inputOrig.json")));
    }

    @Test
    public void testToStringFormat() {
        final DisplayForm attrDF = new AttributeDisplayForm(TITLE, FORM_OF, EXPRESSION, DEFAULT,
                LDM_EXPRESSION, TYPE, ELEMENTS_LINK);

        assertThat(attrDF.toString(), matchesPattern(AttributeDisplayForm.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final DisplayForm attrDF = new AttributeDisplayForm(TITLE, FORM_OF, EXPRESSION, DEFAULT,
                LDM_EXPRESSION, TYPE, ELEMENTS_LINK);
        final DisplayForm deserialized = SerializationUtils.roundtrip(attrDF);

        assertThat(deserialized, jsonEquals(attrDF));
    }

    @Test
    public void testSubclass() {
        class Subclass extends AttributeDisplayForm {
            private Subclass(final String title, final String formOf, final String expression, final boolean isDefault,
                             final String ldmExpression,
                             final String type, final String elements) {
                super(title, formOf, expression, isDefault, ldmExpression, type, elements);
            }
        }
        final Subclass subclass = new Subclass(TITLE, FORM_OF, EXPRESSION, DEFAULT, LDM_EXPRESSION, TYPE,
                ELEMENTS_LINK);

        //noinspection deprecation
        assertThat(subclass.content, is(subclass.attributeContent));
    }
}
