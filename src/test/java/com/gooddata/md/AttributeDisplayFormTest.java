package com.gooddata.md;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

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
        final InputStream stream = getClass().getResourceAsStream("/md/attributeDisplayForm.json");
        final AttributeDisplayForm attrDF = new ObjectMapper().readValue(stream, AttributeDisplayForm.class);
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
        final InputStream stream = getClass().getResourceAsStream("/md/attributeDisplayForm.json");
        final AttributeDisplayForm attrDF = new ObjectMapper().readValue(stream, AttributeDisplayForm.class);
        assertThat(attrDF, serializesToJson("/md/attributeDisplayForm-inputOrig.json"));
    }

}
