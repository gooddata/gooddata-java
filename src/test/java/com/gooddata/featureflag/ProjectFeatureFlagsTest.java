/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.featureflag;

import org.testng.annotations.Test;

import java.io.IOException;

import static com.gooddata.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.testng.AssertJUnit.assertNotNull;

public class ProjectFeatureFlagsTest {

    @Test
    public void testDeserialize() {
        final ProjectFeatureFlags featureFlags = readObjectFromResource(getClass(), "/project/feature-flags.json",
                ProjectFeatureFlags.class);
        assertNotNull(featureFlags);

        assertThat(featureFlags, containsInAnyOrder(
                new ProjectFeatureFlag("myCoolFeature", true),
                new ProjectFeatureFlag("mySuperCoolFeature", true),
                new ProjectFeatureFlag("mySuperSecretFeature", false)));
    }

    @Test
    public void testSerialize() throws IOException {
        final ProjectFeatureFlags projectFeatureFlags = new ProjectFeatureFlags(asList(
                new ProjectFeatureFlag("enabledFeatureFlag", true),
                new ProjectFeatureFlag("disabledFeatureFlag", false)));

        final String serializedFeatureFlags = OBJECT_MAPPER.writeValueAsString(projectFeatureFlags);

        assertThat(serializedFeatureFlags, startsWith("{\"featureFlags\""));
        assertThat(serializedFeatureFlags, containsString("\"key\":\"enabledFeatureFlag\",\"value\":true"));
        assertThat(serializedFeatureFlags, containsString("\"key\":\"disabledFeatureFlag\",\"value\":false"));
    }

    @Test
    public void shouldIterateThroughFlagsInForeach() throws Exception {
        final ProjectFeatureFlags flags = readObjectFromResource(getClass(), "/project/feature-flags.json",
                ProjectFeatureFlags.class);
        for (ProjectFeatureFlag flag : flags) {
            assertThat(flag, isOneOf(
                    new ProjectFeatureFlag("myCoolFeature", true),
                    new ProjectFeatureFlag("mySuperCoolFeature", true),
                    new ProjectFeatureFlag("mySuperSecretFeature", false)));
        }
    }

    @Test
    public void isEnabledShouldReturnCorrectBoolean() throws Exception {
        final ProjectFeatureFlags flags = new ProjectFeatureFlags(asList(
            new ProjectFeatureFlag("enabledFlag", true),
            new ProjectFeatureFlag("disabledFlag", false)
        ));

        assertThat(flags.isEnabled("enabledFlag"), is(true));
        assertThat(flags.isEnabled("disabledFlag"), is(false));
        assertThat(flags.isEnabled("nonexistentFlag"), is(false));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whenNullNameThenIsEnabledShouldThrow() throws Exception {
        final ProjectFeatureFlags flags = new ProjectFeatureFlags(
                singletonList(new ProjectFeatureFlag("enabledFlag", true)));
        flags.isEnabled(null);
    }

    @Test
    public void whenNullArgumentInConstructorThenShouldStillWork() throws Exception {
        final ProjectFeatureFlags flags = new ProjectFeatureFlags(null);
        assertNotNull(flags.iterator());
        assertThat(flags.isEnabled("nonexistentFlag"), is(false));
    }

}