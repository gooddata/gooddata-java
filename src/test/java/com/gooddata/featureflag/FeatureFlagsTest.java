/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.featureflag;

import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.isOneOf;
import static org.testng.AssertJUnit.assertNotNull;

public class FeatureFlagsTest {

    @Test
    public void testDeserialize() {
        final FeatureFlags flags = readObjectFromResource(getClass(), "/gdc/featureFlags.json", FeatureFlags.class);

        assertNotNull(flags);
        assertThat(flags, containsInAnyOrder(
                new FeatureFlag("testFeature", true),
                new FeatureFlag("testFeature2", false)));
    }

    @Test
    public void shouldIterateThroughFlagsInForeach() throws Exception {
        final FeatureFlags flags = readObjectFromResource(getClass(), "/gdc/featureFlags.json", FeatureFlags.class);
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

}
