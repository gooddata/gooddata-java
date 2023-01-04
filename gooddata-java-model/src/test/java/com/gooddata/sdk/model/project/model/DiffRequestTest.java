/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project.model;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DiffRequestTest {

    @Test
    public void testSerialization() throws Exception {
        String valueAsString = OBJECT_MAPPER.writeValueAsString(new DiffRequest("{\"projectModel\":\"xxx\"}"));
        assertThat(valueAsString, is("{\"diffRequest\":{\"targetModel\":{\"projectModel\":\"xxx\"}}}"));
    }
}
