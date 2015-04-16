package com.gooddata.project;

import static com.gooddata.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.testng.AssertJUnit.assertNotNull;

import org.testng.annotations.Test;

import java.io.IOException;

public class ProjectFeatureFlagsTest {

    @Test
    public void testDeserialize() {
        final ProjectFeatureFlags featureFlags = readObjectFromResource(getClass(), "/project/feature-flags.json",
                ProjectFeatureFlags.class);
        assertNotNull(featureFlags);

        assertThat(featureFlags.getItems(), contains(
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

}