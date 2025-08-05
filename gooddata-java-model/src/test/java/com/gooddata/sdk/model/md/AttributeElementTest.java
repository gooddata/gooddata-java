/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class AttributeElementTest {

    private static final String URI = "/gdc/md/PROJECT_ID/obj/1333/elements?id=6959";
    private static final String TITLE = "1165";

    @Test
    public void shouldDeserialize() throws Exception {
        final AttributeElement element = readObjectFromResource("/md/attributeElement.json", AttributeElement.class);
        assertThat(element, is(notNullValue()));

        assertThat(element.getUri(), is(URI));
        assertThat(element.getTitle(), is(TITLE));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final AttributeElement element = readObjectFromResource("/md/attributeElement.json", AttributeElement.class);

        assertThat(element.toString(), matchesPattern(AttributeElement.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void shouldVerifyEquals() throws Exception {
        EqualsVerifier.forClass(AttributeElement.class)
                .usingGetClass()
                .verify();
    }
}