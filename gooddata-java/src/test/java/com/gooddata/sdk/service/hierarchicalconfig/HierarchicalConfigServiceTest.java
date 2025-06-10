/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.hierarchicalconfig;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.hierarchicalconfig.ConfigItem;
import com.gooddata.sdk.model.hierarchicalconfig.ConfigItems;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.GoodDataSettings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.URISyntaxException;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
class HierarchicalConfigServiceTest {

    private static final String PROJECT_ID = "11";
    private static final String CONFIG_ITEM_NAME = "item1";
    private static final String PROJECT_CONFIG_ITEM_URI = "/gdc/projects/11/config/" + CONFIG_ITEM_NAME;

    @Mock
    private Project project;
    @Mock
    private ConfigItem configItem;
    @Mock
    private WebClient webClient;

    private HierarchicalConfigService hierarchicalConfigService;

    @BeforeEach
    void setUp() {
        hierarchicalConfigService = new HierarchicalConfigService(webClient, new GoodDataSettings());
        lenient().when(project.getId()).thenReturn(PROJECT_ID);
    }

    /*--- Project Config ---*/

    @Test
    void whenNullArgThenListProjectConfigItemsShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> hierarchicalConfigService.listProjectConfigItems(null));
    }


    @Test
    void whenEmptyResponseThenListProjectConfigItemsShouldThrow() throws Exception {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(org.mockito.ArgumentMatchers.<URI>any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ConfigItems.class)).thenReturn(Mono.empty());

        assertThrows(GoodDataException.class, () -> hierarchicalConfigService.listProjectConfigItems(project));
    }


    @Test
    void whenClientErrorResponseThenListProjectConfigItemsShouldThrow() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        try {
            when(uriSpec.uri(new URI("/gdc/projects/11/config"))).thenReturn(headersSpec);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ConfigItems.class)).thenReturn(Mono.error(new RuntimeException("error")));

        assertThrows(GoodDataException.class, () -> hierarchicalConfigService.listProjectConfigItems(project));
    }


    @Test
    void whenNullKeyThenGetProjectConfigItemShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> hierarchicalConfigService.getProjectConfigItem(project, null));
    }

    @Test
    void whenNullProjectThenGetProjectConfigItemShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> hierarchicalConfigService.getProjectConfigItem(null, CONFIG_ITEM_NAME));
    }

    @Test
    void whenEmptyResponseThenGetProjectConfigItemShouldThrow() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ConfigItem.class)).thenReturn(Mono.empty());

        assertThrows(GoodDataException.class, () -> hierarchicalConfigService.getProjectConfigItem(project, CONFIG_ITEM_NAME));
    }

    @Test
    void whenClientErrorResponseThenGetProjectConfigItemShouldThrow() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ConfigItem.class)).thenReturn(Mono.error(new RuntimeException("error")));

        assertThrows(GoodDataException.class, () -> hierarchicalConfigService.getProjectConfigItem(project, CONFIG_ITEM_NAME));
    }


    @Test
    void whenNullConfigItemThenSetProjectConfigItemShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> hierarchicalConfigService.setProjectConfigItem(project, null));
    }

    @Test
    void whenNullProjectThenSetProjectConfigItemShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> hierarchicalConfigService.setProjectConfigItem(null, configItem));
    }


    @Test
    void whenEmptyResponseThenSetProjectConfigItemShouldThrow() {
        when(configItem.getUri()).thenReturn(PROJECT_CONFIG_ITEM_URI);

        WebClient.RequestBodyUriSpec putUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        lenient().when(webClient.put()).thenReturn(putUriSpec);
        lenient().when(putUriSpec.uri(anyString())).thenReturn(putUriSpec);
        lenient().when(putUriSpec.bodyValue(any())).thenReturn(headersSpec);
        lenient().when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.toBodilessEntity())
            .thenReturn(Mono.error(new GoodDataException("Expected error")));

        assertThrows(GoodDataException.class, () -> hierarchicalConfigService.setProjectConfigItem(project, configItem));
    }


    @Test
    void whenClientErrorResponseThenSetProjectConfigItemShouldThrow() {
        when(configItem.getUri()).thenReturn(PROJECT_CONFIG_ITEM_URI);

        WebClient.RequestBodyUriSpec putUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        lenient().when(webClient.put()).thenReturn(putUriSpec);
        lenient().when(putUriSpec.uri(anyString())).thenReturn(putUriSpec);
        lenient().when(putUriSpec.bodyValue(any())).thenReturn(headersSpec);
        lenient().when(headersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(new org.springframework.web.reactive.function.client.WebClientResponseException("error", 500, "error", null, null, null)));

        assertThrows(GoodDataException.class, () -> hierarchicalConfigService.setProjectConfigItem(project, configItem));
    }


    @Test
    void whenNullFlagThenRemoveProjectConfigItemShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> hierarchicalConfigService.removeProjectConfigItem(null));
    }

    @Test
    void whenClientErrorResponseThenRemoveProjectConfigItemShouldThrow() {
        when(configItem.getUri()).thenReturn(PROJECT_CONFIG_ITEM_URI);

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.delete()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(new RuntimeException("error")));

        assertThrows(GoodDataException.class, () -> hierarchicalConfigService.removeProjectConfigItem(configItem));
    }


}
