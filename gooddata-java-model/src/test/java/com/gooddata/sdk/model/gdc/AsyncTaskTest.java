/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.gdc;

import org.junit.jupiter.api.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AsyncTaskTest {

    @Test
    public void testDeserialization() throws Exception {
        final AsyncTask asyncTask = readObjectFromResource("/gdc/asyncTask.json", AsyncTask.class);

        assertThat(asyncTask.getUri(), is("POLL-URI"));
    }

    @Test
    public void testSerialization() throws Exception {
        final String json = OBJECT_MAPPER.writeValueAsString(new AsyncTask("foo"));
        final AsyncTask asyncTask = OBJECT_MAPPER.readValue(json, AsyncTask.class);

        assertThat(asyncTask.getUri(), is("foo"));
    }
}
