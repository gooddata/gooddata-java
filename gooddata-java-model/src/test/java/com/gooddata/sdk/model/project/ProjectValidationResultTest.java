/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
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

    @Test
    public void shouldVerifyEquals() throws Exception {
        EqualsVerifier.forClass(ProjectValidationResult.class)
                .usingGetClass()
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}