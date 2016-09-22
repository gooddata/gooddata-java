/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import org.testng.annotations.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.MatcherAssert.assertThat;

public class PullTest {

    @Test
    public void testSerialization() throws Exception {
        final Pull pull = new Pull("DIR");
        assertThat(pull, serializesToJson("/dataset/pull.json"));
    }
}
