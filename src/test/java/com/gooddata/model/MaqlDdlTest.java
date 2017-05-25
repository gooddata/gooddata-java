/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MaqlDdlTest {

    @Test
    public void testSerialization() throws Exception {
        assertThat(new ObjectMapper().writeValueAsString(new MaqlDdl("maqlddl")),
                is("{\"manage\":{\"maql\":\"maqlddl\"}}"));

    }
}
