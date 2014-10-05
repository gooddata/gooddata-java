package com.gooddata.md;

import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Collection;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

public class AttributeTest {

    public static final String TITLE = "Person ID";

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/attribute.json");
        final Attribute attribute = new ObjectMapper().readValue(stream, Attribute.class);
        assertThat(attribute, is(notNullValue()));

        final Collection<DisplayForm> displayForms = attribute.getDisplayForms();
        assertThat(displayForms, is(notNullValue()));
        assertThat(displayForms, hasSize(1));

        final DisplayForm displayForm = displayForms.iterator().next();
        assertThat(displayForm, is(notNullValue()));

        assertThat(displayForm.getFormOf(), is("/gdc/md/PROJECT_ID/obj/DF_FORM_OF_ID"));
        assertThat(displayForm.getExpression(), is("[/gdc/md/PROJECT_ID/obj/DF_EXPRESSION_ID]"));
        assertThat(displayForm.getLdmExpression(), is("[/gdc/md/PROJECT_ID/obj/DF_LDM_EXPRESSION_ID]"));

        final DisplayForm defaultDisplayForm = attribute.getDefaultDisplayForm();
        assertThat(defaultDisplayForm, is(notNullValue()));

        assertThat(defaultDisplayForm.getFormOf(), is("/gdc/md/PROJECT_ID/obj/DF_FORM_OF_ID"));
        assertThat(defaultDisplayForm.getExpression(), is("[/gdc/md/PROJECT_ID/obj/DF_EXPRESSION_ID]"));
        assertThat(defaultDisplayForm.getLdmExpression(), is("[/gdc/md/PROJECT_ID/obj/DF_LDM_EXPRESSION_ID]"));

        final Collection<Key> primaryKeys = attribute.getPrimaryKeys();
        assertThat(primaryKeys, is(Matchers.notNullValue()));
        assertThat(primaryKeys, hasSize(1));
        assertThat(primaryKeys.iterator().next(), is(Matchers.notNullValue()));

        final Collection<Key> foreignKeys = attribute.getForeignKeys();
        assertThat(foreignKeys, is(Matchers.notNullValue()));
        assertThat(foreignKeys, hasSize(1));
        assertThat(foreignKeys.iterator().next(), is(Matchers.notNullValue()));
    }

    @Test
    public void testSerialization() throws Exception {
        final Attribute attribute = new Attribute(TITLE, new Key("/gdc/md/PROJECT_ID/obj/PK_ID", "col"),
                new Key("/gdc/md/PROJECT_ID/obj/FK_ID", "col"));

        assertThat(attribute, serializesToJson("/md/attribute-input.json"));
    }

}
