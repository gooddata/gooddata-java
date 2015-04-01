/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.project;

import static com.gooddata.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.Test;

import java.io.IOException;

public class FeatureFlagTest {

    @Test
    public void testDeserialize() {
        final FeatureFlag featureFlag = readObjectFromResource(getClass(), "/project/feature-flag.json", FeatureFlag.class);
        assertThat(featureFlag.getKey(), is("myCoolFeature"));
        assertTrue(featureFlag.getValue());
        assertThat(featureFlag.getUri(), is("/gdc/projects/PROJECT_ID/projectFeatureFlags/myCoolFeature"));
    }

    @Test
    public void testSerializeDefaultValueTrue() throws IOException {
        checkFeatureFlagSerialization(new FeatureFlag("myFeature"), true);
    }

    @Test
    public void testSerializeWithValueFalse() throws IOException {
        checkFeatureFlagSerialization(new FeatureFlag("myFeature", false), false);
    }


    private void checkFeatureFlagSerialization(FeatureFlag featureFlag, boolean expectedValue) throws IOException {
        final String serializedFeatureFlag = OBJECT_MAPPER.writeValueAsString(featureFlag);

        assertThat(serializedFeatureFlag, startsWith("{\"featureFlag\""));
        assertThat(serializedFeatureFlag, containsString("\"key\":\"myFeature\""));
        assertThat(serializedFeatureFlag, containsString("\"value\":" + expectedValue));
        assertThat(serializedFeatureFlag, not(containsString("\"links\"")));
    }
}