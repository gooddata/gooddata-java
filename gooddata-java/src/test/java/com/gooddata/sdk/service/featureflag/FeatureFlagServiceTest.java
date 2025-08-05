/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.featureflag;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.featureflag.FeatureFlags;
import com.gooddata.sdk.model.featureflag.ProjectFeatureFlag;
import com.gooddata.sdk.model.featureflag.ProjectFeatureFlags;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.GoodDataSettings;
import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.Test; 
import org.junit.jupiter.api.extension.ExtendWith; 
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.reactive.function.client.WebClient; 
import reactor.core.publisher.Mono; 

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FeatureFlagServiceTest {

    private static final String PROJECT_ID = "11";
    private static final String FLAG_NAME = "flag1";
    private static final String FEATURE_FLAGS_URI = "/gdc/internal/projects/11/featureFlags";
    private static final String PROJECT_FEATURE_FLAGS_URI = "/gdc/projects/11/projectFeatureFlags";
    private static final String PROJECT_FEATURE_FLAG_URI = "/gdc/projects/11/projectFeatureFlags/" + FLAG_NAME;

    @Mock
    private Project project;
    @Mock
    private ProjectFeatureFlag projectFeatureFlag;
    @Mock
    private WebClient webClient; 

    private FeatureFlagService service;

    @BeforeEach 
    public void setUp() {
        service = new FeatureFlagService(webClient, new GoodDataSettings()); 
        when(project.getId()).thenReturn(PROJECT_ID);
    }

    @Test
    void whenNullArgThenGetFeatureFlagsShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.listFeatureFlags(null));
    }

    @Test
    void whenEmptyResponseThenGetFeatureFlagsShouldThrow() {
        mockWebClientGet(FEATURE_FLAGS_URI, FeatureFlags.class, Mono.empty());
        assertThrows(GoodDataException.class, () -> service.listFeatureFlags(project));
    }

    @Test
    void whenClientErrorResponseThenGetFeatureFlagsShouldThrow() {
        mockWebClientGet(FEATURE_FLAGS_URI, FeatureFlags.class, Mono.error(new RuntimeException("")));
        assertThrows(GoodDataException.class, () -> service.listFeatureFlags(project));
    }

    @Test
    void whenNullArgThenGetProjectFeatureFlagsShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.listProjectFeatureFlags(null));
    }

    @Test
    void whenEmptyResponseThenGetProjectFeatureFlagsShouldThrow() {
        mockWebClientGet(PROJECT_FEATURE_FLAGS_URI, ProjectFeatureFlags.class, Mono.empty());
        assertThrows(GoodDataException.class, () -> service.listProjectFeatureFlags(project));
    }

    @Test
    void whenClientErrorResponseThenGetProjectFeatureFlagsShouldThrow() {
        mockWebClientGet(PROJECT_FEATURE_FLAGS_URI, ProjectFeatureFlags.class, Mono.error(new RuntimeException("")));
        assertThrows(GoodDataException.class, () -> service.listProjectFeatureFlags(project));
    }

    @Test
    void whenNullKeyThenCreateProjectFeatureFlagShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.createProjectFeatureFlag(project, null));
    }

    @Test
    void whenNullProjectThenCreateProjectFeatureFlagShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.createProjectFeatureFlag(null, projectFeatureFlag));
    }

    @Test
    void whenEmptyResponseThenCreateProjectFeatureFlagShouldThrow() {
        mockWebClientPost(PROJECT_FEATURE_FLAG_URI, projectFeatureFlag, Mono.empty());
        assertThrows(GoodDataException.class, () -> service.createProjectFeatureFlag(project, projectFeatureFlag));
    }

    @Test
    void whenClientErrorResponseThenCreateProjectFeatureFlagShouldThrow() {
        mockWebClientPost(PROJECT_FEATURE_FLAG_URI, projectFeatureFlag, Mono.error(new RuntimeException("")));
        assertThrows(GoodDataException.class, () -> service.createProjectFeatureFlag(project, projectFeatureFlag));
    }

    @Test
    void whenNullKeyThenGetProjectFeatureFlagShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.getProjectFeatureFlag(project, null));
    }

    @Test
    void whenNullProjectThenGetProjectFeatureFlagShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.getProjectFeatureFlag(null, FLAG_NAME));
    }

    @Test
    void whenEmptyResponseThenGetProjectFeatureFlagShouldThrow() {
        mockWebClientGet(PROJECT_FEATURE_FLAG_URI, ProjectFeatureFlag.class, Mono.empty());
        assertThrows(GoodDataException.class, () -> service.getProjectFeatureFlag(project, FLAG_NAME));
    }

    @Test
    void whenClientErrorResponseThenGetProjectFeatureFlagShouldThrow() {
        mockWebClientGet(PROJECT_FEATURE_FLAG_URI, ProjectFeatureFlag.class, Mono.error(new RuntimeException("")));
        assertThrows(GoodDataException.class, () -> service.getProjectFeatureFlag(project, FLAG_NAME));
    }

    @Test
    void whenNullFlagThenUpdateProjectFeatureFlagShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.updateProjectFeatureFlag(null));
    }

    @Test
    void whenEmptyResponseThenUpdateProjectFeatureFlagShouldThrow() {
        when(projectFeatureFlag.getUri()).thenReturn(PROJECT_FEATURE_FLAG_URI);
        mockWebClientPut(PROJECT_FEATURE_FLAG_URI, projectFeatureFlag, Mono.empty());
        assertThrows(GoodDataException.class, () -> service.updateProjectFeatureFlag(projectFeatureFlag));
    }

    @Test
    void whenClientErrorResponseThenUpdateProjectFeatureFlagShouldThrow() {
        when(projectFeatureFlag.getUri()).thenReturn(PROJECT_FEATURE_FLAG_URI);
        mockWebClientPut(PROJECT_FEATURE_FLAG_URI, projectFeatureFlag, Mono.error(new RuntimeException("")));
        assertThrows(GoodDataException.class, () -> service.updateProjectFeatureFlag(projectFeatureFlag));
    }

    @Test
    void whenNullFlagThenDeleteProjectFeatureFlagShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteProjectFeatureFlag(null));
    }

    @Test
    void whenClientErrorResponseThenDeleteProjectFeatureFlagShouldThrow() {
        when(projectFeatureFlag.getUri()).thenReturn(PROJECT_FEATURE_FLAG_URI);
        mockWebClientDelete(PROJECT_FEATURE_FLAG_URI, Mono.error(new RuntimeException("")));
        assertThrows(GoodDataException.class, () -> service.deleteProjectFeatureFlag(projectFeatureFlag));
    }


    private <T> void mockWebClientGet(String uri, Class<T> clazz, Mono<T> result) {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class); 
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);   
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);              

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(clazz)).thenReturn(result);
    }

    private <T> void mockWebClientPost(String uri, T body, Mono<Void> result) {
        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class); 
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);   
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);              

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(uriSpec);
        when(uriSpec.bodyValue(eq(body))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(result);
    }

    private <T> void mockWebClientPut(String uri, T body, Mono<Void> result) {
        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class); 
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);   
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);              

        when(webClient.put()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(uriSpec);
        when(uriSpec.bodyValue(eq(body))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(result);
    }

    private void mockWebClientDelete(String uri, Mono<Void> result) {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class); 
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);   
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);              

        when(webClient.delete()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(result);
    }
}
