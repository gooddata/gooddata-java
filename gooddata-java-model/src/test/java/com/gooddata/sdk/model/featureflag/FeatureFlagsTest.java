/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.featureflag;

import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;

public class FeatureFlagsTest {

    @Test
    public void testDeserialize() {
        final FeatureFlags flags = readObjectFromResource(getClass(), "/featureflag/featureFlags.json", FeatureFlags.class);

        assertNotNull(flags);
        assertThat(flags, containsInAnyOrder(
                new FeatureFlag("testFeature", true),
                new FeatureFlag("testFeature2", false)));
    }

    @Test
    public void testSerialize() {
        final FeatureFlags flags = new FeatureFlags();
        flags.addFlag("testFeature", true);
        flags.addFlag("testFeature2", false);

        assertThat(flags, jsonEquals(resource("featureflag/featureFlags.json")));
    }

    @Test
    public void shouldIterateThroughFlagsInForeach() throws Exception {
        final FeatureFlags flags = readObjectFromResource(getClass(), "/featureflag/featureFlags.json", FeatureFlags.class);
        for (FeatureFlag flag : flags) {
            assertThat(flag, isOneOf(
                    new FeatureFlag("testFeature", true),
                    new FeatureFlag("testFeature2", false)));
        }
    }

    @Test
    public void isEnabledShouldReturnCorrectBoolean() throws Exception {
        final FeatureFlags flags = new FeatureFlags();
        flags.addFlag("enabledFlag", true);
        flags.addFlag("disabledFlag", false);

        assertThat(flags.isEnabled("enabledFlag"), is(true));
        assertThat(flags.isEnabled("disabledFlag"), is(false));
        assertThat(flags.isEnabled("nonexistentFlag"), is(false));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whenNullNameThenIsEnabledShouldThrow() throws Exception {
        final FeatureFlags flags = new FeatureFlags();
        flags.isEnabled(null);
    }

    @Test
    public void testRemoveFlag() throws Exception {
        final FeatureFlags flags = new FeatureFlags();
        flags.addFlag("enabledFlag", true);

        flags.removeFlag("enabledFlag");

        assertFalse(flags.isEnabled("enabledFlag"));
        assertFalse(flags.iterator().hasNext());
    }

    @Test
    public void testToStringFormat() {
        final FeatureFlags flags = new FeatureFlags();

        assertThat(flags.toString(), matchesPattern(FeatureFlags.class.getSimpleName() + "\\[.*\\]"));
    }
}
