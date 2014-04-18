package com.gooddata.md;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class AttributeTest {
    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/attribute.json");
        final Attribute value = new ObjectMapper().readValue(stream, Attribute.class);
        assertThat(value, is(notNullValue()));
        final Attribute.Content content = value.getContent();
        assertThat(content, is(notNullValue()));

        final Collection<DisplayForm> displayForms = content.getDisplayForms();
        assertThat(displayForms, is(notNullValue()));
        assertThat(displayForms, hasSize(1));

        final DisplayForm displayForm = displayForms.iterator().next();
        final DisplayForm.Content displayFormContent = displayForm.getContent();
        assertThat(displayFormContent, is(notNullValue()));

        assertThat(displayFormContent.getFormOf(), is("/gdc/md/vra1wg1m6r0gzl8i8r8y3h1bk0kkzkpo/obj/28"));
        assertThat(displayFormContent.getExpression(), is("[/gdc/md/vra1wg1m6r0gzl8i8r8y3h1bk0kkzkpo/obj/17]"));
        assertThat(displayFormContent.getLdmexpression(), is(""));
    }
}
