/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.gdc;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class UriResponseTest {

    @Test
    public void testDeserialization() throws Exception {
        final UriResponse uriResponse = readObjectFromResource("/gdc/uriResponse.json", UriResponse.class);

        assertThat(uriResponse, is(notNullValue()));
        assertThat(uriResponse.getUri(), is("URI"));
    }

    @Test
    public void testSerialization() throws Exception {
        assertThat(new UriResponse("URI"), jsonEquals(resource("gdc/uriResponse.json")));
    }

    @Test
    public void testSerializable() throws Exception {
        final UriResponse uriResponse = readObjectFromResource("/gdc/uriResponse.json", UriResponse.class);
        final UriResponse deserialized = SerializationUtils.roundtrip(uriResponse);

        assertThat(deserialized, jsonEquals(uriResponse));
    }
}