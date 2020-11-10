/*
 * Copyright (C) 2004-2020, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.maintenance;

import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ExportProjectTokenTest {

    @Test
    public void shouldSerialize() {
        final ExportProjectToken exportProjectToken = new ExportProjectToken("TOKEN123");

        assertThat(exportProjectToken, jsonEquals(resource("md/maintenance/importProject.json")));
    }

    @Test
    public void testToStringFormat() {
        final ExportProjectToken exportProjectToken = new ExportProjectToken("TOKEN123");

        assertThat(exportProjectToken.toString(), matchesPattern(ExportProjectToken.class.getSimpleName() + "\\[.*\\]"));
    }
}