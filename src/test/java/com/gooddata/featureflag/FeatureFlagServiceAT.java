/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.featureflag;

import com.gooddata.AbstractGoodDataAT;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.hasItems;

/**
 * Feature flag acceptance tests.
 */
public class FeatureFlagServiceAT extends AbstractGoodDataAT {

    private static final String PROJECT_FEATURE_FLAG = "testFeatureFlag";

    @Test(groups = "featureFlag", dependsOnGroups = "project")
    public void createProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag = gd.getFeatureFlagService()
                .createProjectFeatureFlag(project, new ProjectFeatureFlag(PROJECT_FEATURE_FLAG));
        checkProjectFeatureFlag(featureFlag, true);
    }

    @Test(groups = "featureFlag", dependsOnMethods = "createProjectFeatureFlag")
    public void listProjectFeatureFlags() throws Exception {
        gd.getFeatureFlagService().createProjectFeatureFlag(project, new ProjectFeatureFlag("mostRecentFeatureFlag"));

        final ProjectFeatureFlags flags = gd.getFeatureFlagService().listProjectFeatureFlags(project);

        assertThat(flags, hasItems(
                new ProjectFeatureFlag("mostRecentFeatureFlag", true),
                new ProjectFeatureFlag(PROJECT_FEATURE_FLAG, true)));
    }

    @Test(groups = "featureFlag", dependsOnMethods = "listProjectFeatureFlags")
    public void listFeatureFlags() throws Exception {
        final FeatureFlags flags = gd.getFeatureFlagService().listFeatureFlags(project);

        assertThat(flags, hasItems(
                new com.gooddata.featureflag.FeatureFlag("mostRecentFeatureFlag", true),
                new com.gooddata.featureflag.FeatureFlag(PROJECT_FEATURE_FLAG, true)));
    }

    @Test(groups = "featureFlag", dependsOnMethods = "createProjectFeatureFlag")
    public void getProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag =
                gd.getFeatureFlagService().getProjectFeatureFlag(project, PROJECT_FEATURE_FLAG);
        checkProjectFeatureFlag(featureFlag, true);
    }

    private void checkProjectFeatureFlag(ProjectFeatureFlag featureFlag, boolean expectedValue) {
        assertThat(featureFlag, is(notNullValue()));
        assertThat(featureFlag.getName(), is(PROJECT_FEATURE_FLAG));
        assertThat(featureFlag.isEnabled(), is(expectedValue));
    }

}
