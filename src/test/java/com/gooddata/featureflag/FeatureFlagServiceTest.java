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
    private static final String PROJECT_FEATURE_FLAGS_URI = "/gdc/projects/11/projectFeatureFlags";

    @Mock
    private Project project;
    @Mock
    private ProjectFeatureFlags projectFeatureFlags;
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

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whenNullArgThenGetProjectFeatureFlagsShouldThrow() throws Exception {
        service.listProjectFeatureFlags(null);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void whenEmptyResponseThenGetProjectFeatureFlagsShouldThrow() throws Exception {
        when(restTemplate.getForObject(new URI(PROJECT_FEATURE_FLAGS_URI), ProjectFeatureFlags.class)).thenReturn(null);
        service.listProjectFeatureFlags(project);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void whenClientErrorResponseThenGetProjectFeatureFlagsShouldThrow() throws Exception {
        when(restTemplate.getForObject(new URI(PROJECT_FEATURE_FLAGS_URI), ProjectFeatureFlags.class))
                .thenThrow(new RestClientException(""));
        service.listProjectFeatureFlags(project);
    }

    @Test
    public void testGetProjectFeatureFlags() throws Exception {
        final ProjectFeatureFlag flag1 = new ProjectFeatureFlag("flag1", true);
        when(restTemplate.getForObject(new URI(PROJECT_FEATURE_FLAGS_URI), ProjectFeatureFlags.class))
                .thenReturn(projectFeatureFlags);
        when(projectFeatureFlags.iterator()).thenReturn(singleton(flag1).iterator());

        final ProjectFeatureFlags flags = service.listProjectFeatureFlags(project);

        assertThat(flags, contains(flag1));
    }

}