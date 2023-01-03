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

public class MaqlDdlTest {

    @Test
    public void testSerialization() throws Exception {
        assertThat(OBJECT_MAPPER.writeValueAsString(new MaqlDdl("maqlddl")),
                is("{\"manage\":{\"maql\":\"maqlddl\"}}"));

    }
}
