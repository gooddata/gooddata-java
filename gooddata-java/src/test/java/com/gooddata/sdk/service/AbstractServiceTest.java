/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.gooddata.sdk.common.GoodDataException;

import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import org.mockito.quality.Strictness;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AbstractServiceTest {

    private AbstractService service;

    @Mock
    private WebClient webClient;

    @BeforeEach
    @SuppressWarnings({"unchecked", "rawtypes"})
    void setUp() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        
        ClientResponse clientResponse = mock(ClientResponse.class);
        when(clientResponse.statusCode()).thenReturn(org.springframework.http.HttpStatus.ACCEPTED);

        when(webClient.get()).thenReturn(uriSpec);

        when(uriSpec.uri(anyString(), (Object[]) any())).thenReturn(headersSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(uriSpec.uri(isA(java.net.URI.class))).thenReturn(headersSpec);
        when(uriSpec.uri(isA(java.util.function.Function.class))).thenReturn(headersSpec);
        when(uriSpec.uri((java.net.URI) isNull())).thenReturn(headersSpec);

        when(headersSpec.exchangeToMono(any())).thenReturn(Mono.just(clientResponse));
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.empty());

        service = new AbstractService(webClient, new GoodDataSettings()) {};
    }


    @Test
    public void pollShouldSucceedWhenUnderTimeout() throws Exception {
        PollHandler<?, ?> handler = mock(PollHandler.class);
        when(handler.getPolling()).thenReturn(java.net.URI.create("http://example.com/poll"));
        when(handler.isFinished(any(ClientResponse.class))).thenReturn(false);
        when(handler.isDone()).thenReturn(false, true);

        service.poll(handler, 5, TimeUnit.SECONDS);
    }

    @Test
    public void pollShouldThrowExceptionWhenOverTimeout() {
        PollHandler<?, ?> handler = mock(PollHandler.class);
        when(handler.getPolling()).thenReturn(java.net.URI.create("http://example.com/poll"));
        assertThrows(GoodDataException.class, () -> service.poll(handler, 5, TimeUnit.SECONDS));
    }
}