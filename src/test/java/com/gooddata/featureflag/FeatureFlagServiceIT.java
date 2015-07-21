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
    private static final String PROJECT_FEATURE_FLAGS_URI_STRING = PROJECT_FEATURE_FLAGS_TEMPLATE.expand(PROJECT_ID).toString();

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
        final String featureFlagName = "myCoolFeature";
        final String projectFeatureFlagUriString = getProjectFeatureFlagUriString(featureFlagName);

        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PROJECT_FEATURE_FLAGS_URI_STRING)
                .respond()
                .withHeader("Location", projectFeatureFlagUriString)
                .withStatus(201);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(projectFeatureFlagUriString)
                .respond()
                .withBody(readStringFromResource("/featureflag/projectFeatureFlag.json"))
                .withStatus(200);

        final ProjectFeatureFlag featureFlag = service.createProjectFeatureFlag(project,
                new ProjectFeatureFlag(featureFlagName));

        assertThat(featureFlag, is(notNullValue()));
        assertThat(featureFlag.getName(), is(featureFlagName));
        assertThat(featureFlag.isEnabled(), is(true));
    }


    private String getProjectFeatureFlagUriString(String featureFlagName) {
        return PROJECT_FEATURE_FLAG_TEMPLATE.expand(PROJECT_ID, featureFlagName).toString();
    }

}