/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ProjectValidationResultTest {

    @Test
    public void testDeserialize() throws Exception {
        final ProjectValidationResult log = readObjectFromResource("/project/project-validationResultLog.json", ProjectValidationResult.class);

        assertThat(log.getCategory(), is("TRANSITIVITY"));
        assertThat(log.getLevel(), is("WARN"));
        assertThat(log.getMessage(), is("There are inconsistent values between %s->%s->%s and %s->%s for %s value '%s'."));
        assertThat(log.getParams(), hasSize(7));

    }

    @Test
    public void testToStringFormat() throws Exception {
        final ProjectValidationResult log = readObjectFromResource("/project/project-validationResultLog.json", ProjectValidationResult.class);

        assertThat(log.toString(), matchesPattern(ProjectValidationResult.class.getSimpleName() + "\\[.*\\]"));
    }
}