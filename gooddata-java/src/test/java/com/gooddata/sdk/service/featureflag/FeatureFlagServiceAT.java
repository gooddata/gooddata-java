/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.featureflag;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.featureflag.FeatureFlag;
import com.gooddata.sdk.model.featureflag.FeatureFlags;
import com.gooddata.sdk.model.featureflag.ProjectFeatureFlag;
import com.gooddata.sdk.model.featureflag.ProjectFeatureFlags;
import com.gooddata.sdk.service.AbstractGoodDataAT;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;     
import org.junit.jupiter.api.Order; 
import org.junit.jupiter.api.Test;  
import org.junit.jupiter.api.TestMethodOrder; 

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Feature flag acceptance tests.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FeatureFlagServiceAT extends AbstractGoodDataAT {

    private static final String PROJECT_FEATURE_FLAG = "flag1";
    private static final String SECOND_FEATURE_FLAG = "flag2";

    @Test
    @Order(1)
    public void createProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag = gd.getFeatureFlagService()
                .createProjectFeatureFlag(project, new ProjectFeatureFlag(PROJECT_FEATURE_FLAG));
        final ProjectFeatureFlag secondFeatureFlag = gd.getFeatureFlagService()
                .createProjectFeatureFlag(project, new ProjectFeatureFlag(SECOND_FEATURE_FLAG, false));

        checkProjectFeatureFlag(featureFlag, PROJECT_FEATURE_FLAG, true);
        checkProjectFeatureFlag(secondFeatureFlag, SECOND_FEATURE_FLAG, false);
    }

    @Test
    @Order(2)
    public void listProjectFeatureFlags() throws Exception {
        final ProjectFeatureFlags flags = gd.getFeatureFlagService().listProjectFeatureFlags(project);

        assertThat(flags, hasItems(
                new ProjectFeatureFlag(SECOND_FEATURE_FLAG, false),
                new ProjectFeatureFlag(PROJECT_FEATURE_FLAG, true)));
    }

    @Test
    @Order(3)
    public void listFeatureFlags() throws Exception {
        final FeatureFlags flags = gd.getFeatureFlagService().listFeatureFlags(project);

        assertThat(flags, Matchers.hasItems(
                new FeatureFlag(SECOND_FEATURE_FLAG, false),
                new FeatureFlag(PROJECT_FEATURE_FLAG, true)));
    }

    @Test
    @Order(4) 
    public void getProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag =
                gd.getFeatureFlagService().getProjectFeatureFlag(project, PROJECT_FEATURE_FLAG);
        checkProjectFeatureFlag(featureFlag, PROJECT_FEATURE_FLAG, true);
    }

    @Test
    @Order(5) 
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

    @Test
    @Order(6)
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

    @Test
    @Order(7)
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
