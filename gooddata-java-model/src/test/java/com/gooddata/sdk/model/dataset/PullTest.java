/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class PullTest {

    @Test
    public void testSerialization() throws Exception {
        final Pull pull = new Pull("DIR");
        assertThat(pull, jsonEquals(resource("dataset/pull.json")));
    }

    @Test
    public void testToStringFormat() {
        final Pull pull = new Pull("DIR");

        assertThat(pull.toString(), matchesPattern(Pull.class.getSimpleName() + "\\[.*\\]"));
    }
}
