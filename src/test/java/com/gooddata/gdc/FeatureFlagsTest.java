/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.testng.AssertJUnit.assertNotNull;

@SuppressWarnings("deprecation")
public class FeatureFlagsTest {

    @Test
    public void testDeserialize() {
        final FeatureFlags featureFlags = readObjectFromResource(getClass(), "/gdc/featureFlags.json",
                FeatureFlags.class);

        assertNotNull(featureFlags);
        assertThat(featureFlags.getFeatureFlags(), hasSize(2));
        assertThat(featureFlags.getFeatureFlags(), contains(
                new FeatureFlag("testFeature", true),
                new FeatureFlag("testFeature2", false)));
    }

}
