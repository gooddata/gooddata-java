/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.featureflag;

import com.gooddata.GoodDataException;
import com.gooddata.sdk.model.featureflag.FeatureFlag;
import com.gooddata.sdk.model.featureflag.FeatureFlags;
import com.gooddata.sdk.model.featureflag.ProjectFeatureFlag;
import com.gooddata.sdk.model.featureflag.ProjectFeatureFlags;
import com.gooddata.sdk.service.AbstractGoodDataAT;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.fail;

/**
 * Feature flag acceptance tests.
 */
public class FeatureFlagServiceAT extends AbstractGoodDataAT {

    private static final String PROJECT_FEATURE_FLAG = "flag1";
    private static final String SECOND_FEATURE_FLAG = "flag2";

    @Test(groups = "featureFlag", dependsOnGroups = "project")
    public void createProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag = gd.getFeatureFlagService()
                .createProjectFeatureFlag(project, new ProjectFeatureFlag(PROJECT_FEATURE_FLAG));
        final ProjectFeatureFlag secondFeatureFlag = gd.getFeatureFlagService()
                .createProjectFeatureFlag(project, new ProjectFeatureFlag(SECOND_FEATURE_FLAG, false));

        checkProjectFeatureFlag(featureFlag, PROJECT_FEATURE_FLAG, true);
        checkProjectFeatureFlag(secondFeatureFlag, SECOND_FEATURE_FLAG, false);
    }

    @Test(groups = "featureFlag", dependsOnMethods = "createProjectFeatureFlag")
    public void listProjectFeatureFlags() throws Exception {
        final ProjectFeatureFlags flags = gd.getFeatureFlagService().listProjectFeatureFlags(project);

        assertThat(flags, hasItems(
                new ProjectFeatureFlag(SECOND_FEATURE_FLAG, false),
                new ProjectFeatureFlag(PROJECT_FEATURE_FLAG, true)));
    }

    @Test(groups = "featureFlag", dependsOnMethods = "listProjectFeatureFlags")
    public void listFeatureFlags() throws Exception {
        final FeatureFlags flags = gd.getFeatureFlagService().listFeatureFlags(project);

        assertThat(flags, Matchers.hasItems(
                new FeatureFlag(SECOND_FEATURE_FLAG, false),
                new FeatureFlag(PROJECT_FEATURE_FLAG, true)));
    }

    @Test(groups = "featureFlag", dependsOnMethods = "createProjectFeatureFlag")
    public void getProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag =
                gd.getFeatureFlagService().getProjectFeatureFlag(project, PROJECT_FEATURE_FLAG);
        checkProjectFeatureFlag(featureFlag, PROJECT_FEATURE_FLAG, true);
    }

    @Test(groups = "featureFlag", dependsOnMethods = "getProjectFeatureFlag")
    public void updateProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag = gd.getFeatureFlagService().getProjectFeatureFlag(project,
                PROJECT_FEATURE_FLAG);

        // disable (update) feature flag
        featureFlag.setEnabled(false);
        final ProjectFeatureFlag disabledFlag = gd.getFeatureFlagService().updateProjectFeatureFlag(featureFlag);
        checkProjectFeatureFlag(disabledFlag, PROJECT_FEATURE_FLAG, false);

        // enable again
        featureFlag.setEnabled(true);
        final ProjectFeatureFlag enabledFlag = gd.getFeatureFlagService().updateProjectFeatureFlag(featureFlag);
        checkProjectFeatureFlag(enabledFlag, PROJECT_FEATURE_FLAG , true);
    }

    @Test(groups = "featureFlag", dependsOnMethods = "createProjectFeatureFlag")
    public void deleteProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag =
                gd.getFeatureFlagService().createProjectFeatureFlag(project,
                        new ProjectFeatureFlag("settingName1"));

        gd.getFeatureFlagService().deleteProjectFeatureFlag(featureFlag);
        try {
            gd.getFeatureFlagService().getProjectFeatureFlag(project, "settingName1");
            fail();
        } catch (GoodDataException e) {
            assertThat(e.getMessage(), containsString("Unable to get project feature flag:"));
        }
    }

    @Test(groups = "featureFlag", dependsOnMethods = "updateProjectFeatureFlag")
    public void changeProjectFeatureFlag() throws Exception {
        final FeatureFlagService featureFlagService = gd.getFeatureFlagService();
        ProjectFeatureFlag featureFlag = featureFlagService.getProjectFeatureFlag(project, PROJECT_FEATURE_FLAG);
        boolean value = !featureFlag.isEnabled();
        featureFlag.setEnabled(value);
        featureFlagService.updateProjectFeatureFlag(featureFlag);
        checkProjectFeatureFlag(featureFlag, PROJECT_FEATURE_FLAG, value);
    }

    private void checkProjectFeatureFlag(final ProjectFeatureFlag featureFlag, final String expectedName,
                                         final boolean expectedValue) {
        assertThat(featureFlag, is(notNullValue()));
        assertThat(featureFlag.getName(), is(expectedName));
        assertThat(featureFlag.isEnabled(), is(expectedValue));
    }

}
