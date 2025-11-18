/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.auth;

import org.springframework.http.HttpHeaders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

public class CredentialManagerTest {

    private CredentialManager credentialManager;

    @BeforeMethod
    public void setUp() {
        credentialManager = new CredentialManager();
    }

    @Test
    public void testExtractAuthHeadersWithSSTToken() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-GDC-AuthSST", "test-sst-token");
        headers.set("X-GDC-AuthTT", "test-tt-token");

        // When
        Map<String, String> extracted = credentialManager.extractAuthHeaders(headers);

        // Then
        assertEquals(extracted.get("X-GDC-AuthSST"), "test-sst-token");
        assertEquals(extracted.get("X-GDC-AuthTT"), "test-tt-token");
        assertTrue(credentialManager.hasAuthentication());
    }

    @Test
    public void testExtractAuthHeadersWithCookies() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.set("Set-Cookie", "GDCAuthSST=token123; Path=/; HttpOnly");

        // When
        Map<String, String> extracted = credentialManager.extractAuthHeaders(headers);

        // Then
        assertEquals(extracted.get("Cookie"), "GDCAuthSST=token123; Path=/; HttpOnly");
        assertTrue(credentialManager.hasAuthentication());
    }

    @Test
    public void testExtractAuthHeadersWithNullHeaders() {
        // When
        Map<String, String> extracted = credentialManager.extractAuthHeaders(null);

        // Then
        assertTrue(extracted.isEmpty());
        assertFalse(credentialManager.hasAuthentication());
    }

    @Test
    public void testSetAndGetAuthHeaders() {
        // Given
        Map<String, String> authHeaders = Map.of(
            "Authorization", "Bearer token123",
            "X-Custom-Header", "custom-value"
        );

        // When
        credentialManager.setAuthHeaders(authHeaders);

        // Then
        assertEquals(credentialManager.getAuthHeader("Authorization"), "Bearer token123");
        assertEquals(credentialManager.getAuthHeader("X-Custom-Header"), "custom-value");
        assertTrue(credentialManager.hasAuthentication());
    }

    @Test
    public void testBasicAuthentication() {
        // When
        credentialManager.setBasicAuthentication("testuser", "testpass");

        // Then
        assertEquals(credentialManager.getUsername(), "testuser");
        assertEquals(credentialManager.getPassword(), "testpass");
    }

    @Test
    public void testClearAuthentication() {
        // Given
        credentialManager.setAuthHeaders(Map.of("Authorization", "Bearer token123"));
        credentialManager.setBasicAuthentication("testuser", "testpass");
        assertTrue(credentialManager.hasAuthentication());

        // When
        credentialManager.clearAuthentication();

        // Then
        assertFalse(credentialManager.hasAuthentication());
        assertNull(credentialManager.getUsername());
        assertNull(credentialManager.getPassword());
        assertTrue(credentialManager.getAuthHeaders().isEmpty());
    }

    @Test
    public void testRefreshCredentials() {
        // Given
        credentialManager.setAuthHeaders(Map.of("X-GDC-AuthSST", "old-token"));
        credentialManager.setBasicAuthentication("testuser", "testpass");
        assertTrue(credentialManager.hasAuthentication());

        // When
        credentialManager.refreshCredentials();

        // Then
        assertEquals(credentialManager.getUsername(), "testuser");
        assertEquals(credentialManager.getPassword(), "testpass");
        // Auth headers should be cleared but basic credentials preserved
        assertNull(credentialManager.getAuthHeader("X-GDC-AuthSST"));
    }

    @Test
    public void testGetAuthenticationState() {
        // Given
        credentialManager.setBasicAuthentication("testuser", "testpass");
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-GDC-AuthSST", "test-token");
        credentialManager.extractAuthHeaders(headers);

        // When
        Map<String, String> state = credentialManager.getAuthenticationState();

        // Then
        assertEquals(state.get("username"), "testuser");
        assertEquals(state.get("password"), "testpass");
        assertEquals(state.get("sst"), "test-token");
        
        // Verify it's a safe copy
        state.put("test", "value");
        assertNull(credentialManager.getAuthenticationState().get("test"));
    }

    @Test
    public void testHasAuthenticationVariousCases() {
        // Initially no authentication
        assertFalse(credentialManager.hasAuthentication());

        // With SST token
        credentialManager.setAuthHeaders(Map.of("X-GDC-AuthSST", "token"));
        assertTrue(credentialManager.hasAuthentication());

        credentialManager.clearAuthentication();

        // With cookies
        credentialManager.setAuthHeaders(Map.of("Cookie", "session=123"));
        assertTrue(credentialManager.hasAuthentication());

        credentialManager.clearAuthentication();

        // With authorization header
        credentialManager.setAuthHeaders(Map.of("Authorization", "Bearer token"));
        assertTrue(credentialManager.hasAuthentication());
    }
}