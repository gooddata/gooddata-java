package com.gooddata.md;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class AttributeDisplayFormTest {

    public static final String FORM_OF = "/gdc/md/PROJECT_ID/obj/DF_FORM_OF_ID";
    public static final String EXPRESSION = "[/gdc/md/PROJECT_ID/obj/DF_EXPRESSION_ID]";
    public static final Integer DEFAULT = 0;
    public static final String LDM_EXPRESSION = "";

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/attributeDisplayForm.json");
        final AttributeDisplayForm attrDF = new ObjectMapper().readValue(stream, AttributeDisplayForm.class);
        assertThat(attrDF, is(notNullValue()));

        assertThat(attrDF.getFormOf(), is(FORM_OF));
        assertThat(attrDF.getExpression(), is(EXPRESSION));
        assertThat(attrDF.getDefault(), is(DEFAULT));
        assertThat(attrDF.getLdmExpression(), is(LDM_EXPRESSION));
    }

    @Test
    public void testSerialization() throws Exception {
        final DisplayForm attrDF = new AttributeDisplayForm("Person Name", FORM_OF, EXPRESSION, DEFAULT,
                LDM_EXPRESSION);

        assertThat(attrDF, serializesToJson("/md/attributeDisplayForm-input.json"));
    }

}
