/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.featureflag;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ProjectFeatureFlagTest {

    @Test
    public void testDefaultValue() throws Exception {
        final ProjectFeatureFlag flag = new ProjectFeatureFlag("test");
        assertThat(flag.isEnabled(), is(true));
    }

    @Test
    public void testCustomValue() throws Exception {
        final ProjectFeatureFlag flag = new ProjectFeatureFlag("test", false);
        assertThat(flag.isEnabled(), is(false));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEmptyName() throws Exception {
        new ProjectFeatureFlag(" ");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEmptyNameWithValue() throws Exception {
        new ProjectFeatureFlag(" ", false);
    }

    @Test
    public void testToStringFormat() {
        final ProjectFeatureFlag flag = new ProjectFeatureFlag("test", false);

        assertThat(flag.toString(), matchesPattern(ProjectFeatureFlag.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void shouldVerifyEquals() throws Exception {
        EqualsVerifier.forClass(ProjectFeatureFlag.class)
                .usingGetClass()
                .suppress(Warning.NONFINAL_FIELDS)
                .withIgnoredFields("links")
                .verify();
    }
}