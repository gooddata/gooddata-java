/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Collection;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class AttributeTest {

    public static final String TITLE = "Person ID";

    @Test
    public void shouldDeserialize() throws Exception {
        final Attribute attribute = readObjectFromResource("/md/attribute.json", Attribute.class);
        assertThat(attribute, is(notNullValue()));
        assertThat(attribute.getId(), is("28"));

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

        assertThat(attribute.hasDimension(), is(true));
        assertThat(attribute.getDimensionUri(), is("/gdc/md/PROJECT_ID/obj/DIM_ID"));

        assertThat(attribute.getDirection(), is("asc"));
        assertThat(attribute.getSort(), is("pk"));
        assertThat(attribute.isSortedByPk(), is(true));
        assertThat(attribute.isSortedByUsedDf(), is(false));
        assertThat(attribute.isSortedByLinkedDf(), is(false));
        assertThat(attribute.getType(), is("GDC.time.date"));
        assertThat(attribute.getLinkedDisplayFormUri(), is("/gdc/md/PROJECT_ID/obj/DF_LINK"));
        assertThat(attribute.getCompositeAttribute(), hasSize(0));
        assertThat(attribute.getCompositeAttributePk(), hasSize(0));
        assertThat(attribute.getFolders(), hasSize(0));
        assertThat(attribute.getGrain(), hasSize(0));
        assertThat(attribute.getRelations(), hasSize(0));
    }

    @Test
    public void testSerialization() throws Exception {
        final Attribute attribute = new Attribute(TITLE, new Key("/gdc/md/PROJECT_ID/obj/PK_ID", "col"),
                new Key("/gdc/md/PROJECT_ID/obj/FK_ID", "col"));

        assertThat(attribute, jsonEquals(resource("md/attribute-input.json")));
    }

    @Test
    public void shouldSerializeSameAsDeserializationInput() throws Exception {
        final Attribute attribute = readObjectFromResource("/md/attribute.json", Attribute.class);
        assertThat(attribute, jsonEquals(resource("md/attribute-inputOrig.json")));
    }

    @Test
    public void shouldDeserializeAttributeWithSort() throws Exception {
        final Attribute attribute = readObjectFromResource("/md/attribute-sortDf.json", Attribute.class);

        assertThat(attribute.getSort(), is("/gdc/md/PROJECT_ID/obj/1806"));
        assertThat(attribute.isSortedByLinkedDf(), is(true));
        assertThat(attribute.isSortedByUsedDf(), is(false));
        assertThat(attribute.isSortedByPk(), is(false));
    }
}
