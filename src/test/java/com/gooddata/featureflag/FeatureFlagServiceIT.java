/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.featureflag;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.project.Project;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.gooddata.featureflag.ProjectFeatureFlag.PROJECT_FEATURE_FLAG_TEMPLATE;
import static com.gooddata.featureflag.ProjectFeatureFlags.PROJECT_FEATURE_FLAGS_TEMPLATE;
import static com.gooddata.util.ResourceUtils.readFromResource;
import static com.gooddata.util.ResourceUtils.readStringFromResource;
import static net.jadler.Jadler.onRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class FeatureFlagServiceIT extends AbstractGoodDataIT {

    private FeatureFlagService service;
    private Project project;

    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String PROJECT_FEATURE_FLAGS_URI_STRING = PROJECT_FEATURE_FLAGS_TEMPLATE.expand(PROJECT_ID)
            .toString();
    private static final String FEATURE_FLAG_NAME = "myCoolFeature";
    private static final String PROJECT_FEATURE_FLAG_URI_STRING = PROJECT_FEATURE_FLAG_TEMPLATE
            .expand(PROJECT_ID, FEATURE_FLAG_NAME).toString();

    @BeforeMethod
    public void setUp() throws Exception {
        service = gd.getFeatureFlagService();
        project = MAPPER.readValue(readFromResource("/project/project.json"), Project.class);
    }

    @Test
    public void listFeatureFlagsShouldReturnAggregatedFlagsForProject() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/internal/projects/PROJECT_ID/featureFlags")
                .respond()
                .withBody(readStringFromResource("/featureflag/featureFlags.json"))
                .withStatus(200);

        final FeatureFlags flags = service.listFeatureFlags(project);

        assertThat(flags, containsInAnyOrder(
                new FeatureFlag("testFeature", true),
                new FeatureFlag("testFeature2", false)));
    }

    @Test
    public void listProjectFeatureFlagsShouldReturnProjectFeatureFlags() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/projectFeatureFlags")
                .respond()
                .withBody(readStringFromResource("/featureflag/projectFeatureFlags.json"))
                .withStatus(200);

        final ProjectFeatureFlags flags = service.listProjectFeatureFlags(project);

        assertThat(flags, containsInAnyOrder(
                new ProjectFeatureFlag("myCoolFeature", true),
                new ProjectFeatureFlag("mySuperCoolFeature", true),
                new ProjectFeatureFlag("mySuperSecretFeature", false)));
    }

    @Test
    public void createProjectFeatureFlagShouldCreateAndReturnNewProjectFeatureFlag() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PROJECT_FEATURE_FLAGS_URI_STRING)
                .respond()
                .withHeader("Location", service.getProjectFeatureFlagUri(project, FEATURE_FLAG_NAME).toString())
                .withStatus(201);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROJECT_FEATURE_FLAG_URI_STRING)
                .respond()
                .withBody(readStringFromResource("/featureflag/projectFeatureFlag.json"))
                .withStatus(200);

        final ProjectFeatureFlag featureFlag = service.createProjectFeatureFlag(project,
                new ProjectFeatureFlag(FEATURE_FLAG_NAME));

        assertThat(featureFlag, is(notNullValue()));
        assertThat(featureFlag.getName(), is(FEATURE_FLAG_NAME));
        assertThat(featureFlag.isEnabled(), is(true));
    }

    @Test
    public void getProjectFeatureFlagShouldReturnProjectFeatureFlag() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROJECT_FEATURE_FLAG_URI_STRING)
                .respond()
                .withBody(readStringFromResource("/featureflag/projectFeatureFlag.json"))
                .withStatus(200);

        final ProjectFeatureFlag flag = service.getProjectFeatureFlag(project, FEATURE_FLAG_NAME);

        assertThat(flag, is(new ProjectFeatureFlag(FEATURE_FLAG_NAME, true)));
    }

    @Test
    public void updateProjectFeatureFlagShouldUpdateAndReturnUpdatedProjectFeatureFlag() throws Exception {

        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo(PROJECT_FEATURE_FLAG_URI_STRING)
                .respond()
                .withStatus(200);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROJECT_FEATURE_FLAG_URI_STRING)
                .respond()
                .withBody(readStringFromResource("/featureflag/projectFeatureFlag.json"))
                .withStatus(200);

        final ProjectFeatureFlag flag = MAPPER
                .readValue(readFromResource("/featureflag/projectFeatureFlag.json"), ProjectFeatureFlag.class);
        final ProjectFeatureFlag featureFlag = service.updateProjectFeatureFlag(flag);

        assertThat(featureFlag, is(notNullValue()));
        assertThat(featureFlag.getName(), is(FEATURE_FLAG_NAME));
        assertThat(featureFlag.isEnabled(), is(true));
    }

    @Test
    public void deleteProjectFeatureFlagShouldRemoveIt() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(PROJECT_FEATURE_FLAG_URI_STRING)
                .respond()
                .withStatus(200);

        final ProjectFeatureFlag flag = MAPPER
                .readValue(readFromResource("/featureflag/projectFeatureFlag.json"), ProjectFeatureFlag.class);
        service.deleteProjectFeatureFlag(flag);
    }

}