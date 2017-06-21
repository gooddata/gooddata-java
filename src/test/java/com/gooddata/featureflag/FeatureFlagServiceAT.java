/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.featureflag;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.AssertJUnit.fail;

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

        assertThat(flags, hasItems(
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
        // can not check value because of eventual consistency of this API
        // checkProjectFeatureFlag(disabledFlag, false);

        // enable again
        featureFlag.setEnabled(true);
        final ProjectFeatureFlag enabledFlag = gd.getFeatureFlagService().updateProjectFeatureFlag(featureFlag);
        // can not check value because of eventual consistency of this API
        // checkProjectFeatureFlag(enabledFlag, true);
    }

    @Test(groups = "featureFlag", dependsOnMethods = "createProjectFeatureFlag")
    public void deleteProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag =
                gd.getFeatureFlagService().createProjectFeatureFlag(project,
                        new ProjectFeatureFlag("project_1"));

        gd.getFeatureFlagService().deleteProjectFeatureFlag(featureFlag);

        try {
            gd.getFeatureFlagService().getProjectFeatureFlag(project, featureFlag.getName());
            fail("Feature flag has not been deleted properly. HTTP status NOT FOUND expected.");
        } catch (GoodDataException e) {
            assertThat(((GoodDataRestException)e.getCause()).getStatusCode(), is(HttpStatus.NOT_FOUND.value()));
        }
    }


    private void checkProjectFeatureFlag(final ProjectFeatureFlag featureFlag, final String expectedName,
                                         final boolean expectedValue) {
        assertThat(featureFlag, is(notNullValue()));
        assertThat(featureFlag.getName(), is(expectedName));
        assertThat(featureFlag.isEnabled(), is(expectedValue));
    }

}
