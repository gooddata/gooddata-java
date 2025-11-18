/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class RestTemplateHttpClientAdapterTest {

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock 
    private RequestCallback mockRequestCallback;

    @Mock
    private ResponseExtractor<String> mockResponseExtractor;

    private RestTemplateHttpClientAdapter adapter;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new RestTemplateHttpClientAdapter(mockRestTemplate);
    }

    @Test
    public void testExecuteWithUrl() {
        // Given
        String url = "https://example.com/api/test";
        String expectedResponse = "test response";
        when(mockRestTemplate.execute(url, HttpMethod.GET, mockRequestCallback, mockResponseExtractor))
                .thenReturn(expectedResponse);

        // When
        String result = adapter.execute(url, HttpMethod.GET, mockRequestCallback, mockResponseExtractor);

        // Then
        assertEquals(result, expectedResponse);
        verify(mockRestTemplate).execute(url, HttpMethod.GET, mockRequestCallback, mockResponseExtractor);
    }

    @Test
    public void testExecuteWithUri() {
        // Given
        URI uri = URI.create("https://example.com/api/test");
        String expectedResponse = "test response";
        when(mockRestTemplate.execute(uri, HttpMethod.POST, mockRequestCallback, mockResponseExtractor))
                .thenReturn(expectedResponse);

        // When
        String result = adapter.execute(uri, HttpMethod.POST, mockRequestCallback, mockResponseExtractor);

        // Then
        assertEquals(result, expectedResponse);
        verify(mockRestTemplate).execute(uri, HttpMethod.POST, mockRequestCallback, mockResponseExtractor);
    }

    @Test
    public void testGetRestTemplate() {
        // When
        RestTemplate result = adapter.getRestTemplate();

        // Then
        assertSame(result, mockRestTemplate);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, 
          expectedExceptionsMessageRegExp = ".*restTemplate.*")
    public void testConstructorWithNullRestTemplate() {
        new RestTemplateHttpClientAdapter(null);
    }
}