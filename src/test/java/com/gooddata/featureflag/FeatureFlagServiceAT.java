/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.featureflag;

import com.gooddata.AbstractGoodDataAT;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

/**
 * Feature flag acceptance tests.
 */
public class FeatureFlagServiceAT extends AbstractGoodDataAT {

    private static final String PROJECT_FEATURE_FLAG = "testFeatureFlag";

    @Test(groups = "featureFlag", dependsOnGroups = "project")
    public void listProjectFeatureFlags() throws Exception {
        gd.getProjectService().createFeatureFlag(project,
                new com.gooddata.project.ProjectFeatureFlag("mostRecentFeatureFlag"));

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

}
