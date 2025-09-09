/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import java.util.Collection;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class DimensionTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final Dimension dimension = readObjectFromResource("/md/dimension.json", Dimension.class);
        assertThat(dimension, is(notNullValue()));

        final Collection<NestedAttribute> attributes = dimension.getAttributes();
        assertThat(attributes, is(notNullValue()));
        assertThat(attributes, hasSize(2));
    }

    @Test
    public void shouldSerialize() throws Exception {
        final Dimension dimension = new Dimension("Dimension");
        assertThat(dimension, jsonEquals(resource("md/dimension-input.json")));

    }

    @Test
    public void testToStringFormat() {
        final Dimension dimension = new Dimension("Dimension");

        assertThat(dimension.toString(), matchesPattern(Dimension.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final Dimension dimension = new Dimension("Dimension");
        final Dimension deserialized = SerializationUtils.roundtrip(dimension);

        assertThat(deserialized, jsonEquals(dimension));
    }
}
