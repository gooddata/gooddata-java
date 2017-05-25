/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MaqlDmlTest {

    @Test
    public void testSerialization() throws Exception {
        final String json = IOUtils.toString(getClass().getResourceAsStream("/dataset/maqlDml.json"), "UTF-8");
        assertThat(new ObjectMapper().writeValueAsString(new MaqlDml("maqlddl")),
                is(json));
    }
}
