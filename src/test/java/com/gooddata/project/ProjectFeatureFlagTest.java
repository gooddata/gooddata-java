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

public class ProjectFeatureFlagTest {

    @Test
    public void testDeserialize() {
        final ProjectFeatureFlag featureFlag = readObjectFromResource(getClass(), "/project/feature-flag.json", ProjectFeatureFlag.class);
        assertThat(featureFlag.getName(), is("myCoolFeature"));
        assertTrue(featureFlag.getEnabled());
        assertThat(featureFlag.getUri(), is("/gdc/projects/PROJECT_ID/projectFeatureFlags/myCoolFeature"));
    }

    @Test
    public void testSerializeDefaultValueTrue() throws IOException {
        checkFeatureFlagSerialization(new ProjectFeatureFlag("myFeature"), true);
    }

    @Test
    public void testSerializeWithValueFalse() throws IOException {
        checkFeatureFlagSerialization(new ProjectFeatureFlag("myFeature", false), false);
    }


    private void checkFeatureFlagSerialization(ProjectFeatureFlag featureFlag, boolean expectedValue) throws IOException {
        final String serializedFeatureFlag = OBJECT_MAPPER.writeValueAsString(featureFlag);

        assertThat(serializedFeatureFlag, startsWith("{\"featureFlag\""));
        assertThat(serializedFeatureFlag, containsString("\"key\":\"myFeature\""));
        assertThat(serializedFeatureFlag, containsString("\"value\":" + expectedValue));
        assertThat(serializedFeatureFlag, not(containsString("\"links\"")));
    }
}