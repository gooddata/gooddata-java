/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.featureflag;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.oneOf;
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
    public void shouldIterateThroughFlagsInForeach() {
        final FeatureFlags flags = readObjectFromResource(getClass(), "/featureflag/featureFlags.json", FeatureFlags.class);
        for (FeatureFlag flag : flags) {
            assertThat(flag, is(oneOf(
                    new FeatureFlag("testFeature", true),
                    new FeatureFlag("testFeature2", false))));
        }
    }

    @Test
    public void isEnabledShouldReturnCorrectBoolean() {
        final FeatureFlags flags = new FeatureFlags();
        flags.addFlag("enabledFlag", true);
        flags.addFlag("disabledFlag", false);

        assertThat(flags.isEnabled("enabledFlag"), is(true));
        assertThat(flags.isEnabled("disabledFlag"), is(false));
        assertThat(flags.isEnabled("nonexistentFlag"), is(false));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whenNullNameThenIsEnabledShouldThrow() {
        final FeatureFlags flags = new FeatureFlags();
        flags.isEnabled(null);
    }

    @Test
    public void testRemoveFlag() {
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

