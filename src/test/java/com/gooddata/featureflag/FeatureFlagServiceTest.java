/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.featureflag;

import com.gooddata.GoodDataException;
import com.gooddata.project.Project;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;

public class FeatureFlagServiceTest {

    private static final String PROJECT_ID = "11";
    private static final String FEATURE_FLAGS_URI = "/gdc/internal/projects/11/featureFlags";

    @Mock
    private Project project;
    @Mock
    private FeatureFlags featureFlags;
    @Mock
    private RestTemplate restTemplate;

    private FeatureFlagService service;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new FeatureFlagService(restTemplate);
        when(project.getId()).thenReturn(PROJECT_ID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whenNullArgThenGetFeatureFlagsShouldThrow() throws Exception {
        service.listFeatureFlags(null);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void whenEmptyResponseThenGetFeatureFlagsShouldThrow() throws Exception {
        when(restTemplate.getForObject(new URI(FEATURE_FLAGS_URI), FeatureFlags.class)).thenReturn(null);
        service.listFeatureFlags(project);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void whenClientErrorResponseThenGetFeatureFlagsShouldThrow() throws Exception {
        when(restTemplate.getForObject(new URI(FEATURE_FLAGS_URI), FeatureFlags.class))
                .thenThrow(new RestClientException(""));
        service.listFeatureFlags(project);
    }

    @Test
    public void testGetFeatureFlags() throws Exception {
        final FeatureFlag flag1 = new FeatureFlag("flag1", true);
        when(restTemplate.getForObject(new URI(FEATURE_FLAGS_URI), FeatureFlags.class)).thenReturn(featureFlags);
        when(featureFlags.iterator()).thenReturn(singleton(flag1).iterator());

        final FeatureFlags flags = service.listFeatureFlags(project);

        assertThat(flags, contains(flag1));
    }

}