package com.gooddata.md;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AttributeDisplayFormTest {
    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/attributeDisplayForm.json");
        final AttributeDisplayForm value = new ObjectMapper().readValue(stream, AttributeDisplayForm.class);
        assertThat(value, is(notNullValue()));
        final DisplayForm.Content content = value.getContent();
        assertThat(content, is(notNullValue()));
        assertThat(content.getFormOf(), is("/gdc/md/vra1wg1m6r0gzl8i8r8y3h1bk0kkzkpo/obj/28"));
        assertThat(content.getExpression(), is("[/gdc/md/vra1wg1m6r0gzl8i8r8y3h1bk0kkzkpo/obj/17]"));
        assertThat(content.getDefaultValue(), is("0"));
        assertThat(content.getLdmexpression(), is(""));
    }
}
