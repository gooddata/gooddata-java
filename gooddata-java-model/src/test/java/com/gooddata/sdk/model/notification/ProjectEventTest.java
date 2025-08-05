/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.notification;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class ProjectEventTest {

    @Test
    public void testSerialization() {
        final ProjectEvent projectEvent = new ProjectEvent("etl.test");
        projectEvent.setParameter("text", "cool");
        assertThat(projectEvent, jsonEquals(resource("notification/projectEvent.json")));
    }

    @Test
    public void testToStringFormat() {
        final ProjectEvent projectEvent = new ProjectEvent("etl.test");

        assertThat(projectEvent.toString(), matchesPattern(ProjectEvent.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void shouldVerifyEquals() throws Exception {
        EqualsVerifier.forClass(ProjectEvent.class)
                .usingGetClass()
                .verify();
    }

}