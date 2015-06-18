/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.featureflag;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.project.Project;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static com.gooddata.util.ResourceUtils.readStringFromResource;
import static net.jadler.Jadler.onRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class FeatureFlagServiceIT extends AbstractGoodDataIT {

    private FeatureFlagService service;
    private Project project;

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
                .withBody(readStringFromResource("/gdc/featureFlags.json"))
                .withStatus(200);

        final FeatureFlags flags = service.listFeatureFlags(project);

        assertThat(flags, containsInAnyOrder(
                new FeatureFlag("testFeature", true),
                new FeatureFlag("testFeature2", false)));
    }
}