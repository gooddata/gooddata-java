/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages credentials and authentication state sharing between the main SDK
 * (using RestClient with HttpClient 5.x) and the WebDAV service (using Sardine with HttpClient 4.x).
 * 
 * This class extracts authentication tokens and cookies from HTTP responses and
 * provides them in a format that can be shared across different HTTP client implementations.
 */
public class CredentialManager {

    private static final Logger logger = LoggerFactory.getLogger(CredentialManager.class);

    private final Map<String, String> sharedHeaders = new ConcurrentHashMap<>();
    private final Map<String, String> authenticationState = new ConcurrentHashMap<>();

    // Common authentication header names
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String COOKIE_HEADER = "Cookie";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final String SST_HEADER = "X-GDC-AuthSST";
    private static final String TT_HEADER = "X-GDC-AuthTT";

    /**
     * Extract authentication information from HTTP response headers
     * 
     * @param responseHeaders HTTP response headers
     * @return authentication headers that can be shared
     */
    public Map<String, String> extractAuthHeaders(HttpHeaders responseHeaders) {
        Map<String, String> extractedHeaders = new HashMap<>();
        
        if (responseHeaders == null) {
            return extractedHeaders;
        }

        // Extract GoodData-specific authentication headers
        String sstToken = responseHeaders.getFirst(SST_HEADER);
        if (sstToken != null && !sstToken.trim().isEmpty()) {
            extractedHeaders.put(SST_HEADER, sstToken);
            authenticationState.put("sst", sstToken);
            logger.debug("Extracted SST token from response");
        }

        String ttToken = responseHeaders.getFirst(TT_HEADER);
        if (ttToken != null && !ttToken.trim().isEmpty()) {
            extractedHeaders.put(TT_HEADER, ttToken);
            authenticationState.put("tt", ttToken);
            logger.debug("Extracted TT token from response");
        }

        // Extract cookies that might contain authentication information
        String setCookieHeader = responseHeaders.getFirst(SET_COOKIE_HEADER);
        if (setCookieHeader != null && !setCookieHeader.trim().isEmpty()) {
            extractedHeaders.put(COOKIE_HEADER, setCookieHeader);
            authenticationState.put("cookies", setCookieHeader);
            logger.debug("Extracted authentication cookies from response");
        }

        // Store shared headers for future use
        sharedHeaders.putAll(extractedHeaders);
        
        return extractedHeaders;
    }

    /**
     * Get current authentication headers for sharing with WebDAV service
     * 
     * @return current authentication headers
     */
    public Map<String, String> getAuthHeaders() {
        return new HashMap<>(sharedHeaders);
    }

    /**
     * Set authentication headers from external source (e.g., login response)
     * 
     * @param headers authentication headers to set
     */
    public void setAuthHeaders(Map<String, String> headers) {
        if (headers != null) {
            sharedHeaders.putAll(headers);
            logger.debug("Updated shared authentication headers: {} keys", headers.size());
        }
    }

    /**
     * Get a specific authentication header value
     * 
     * @param headerName header name
     * @return header value or null if not found
     */
    public String getAuthHeader(String headerName) {
        return sharedHeaders.get(headerName);
    }

    /**
     * Check if authentication information is available
     * 
     * @return true if authentication headers are present
     */
    public boolean hasAuthentication() {
        return !sharedHeaders.isEmpty() && 
               (sharedHeaders.containsKey(SST_HEADER) || 
                sharedHeaders.containsKey(COOKIE_HEADER) ||
                sharedHeaders.containsKey(AUTHORIZATION_HEADER));
    }

    /**
     * Clear all authentication information
     */
    public void clearAuthentication() {
        sharedHeaders.clear();
        authenticationState.clear();
        logger.debug("Cleared all authentication information");
    }

    /**
     * Get authentication state for debugging/monitoring
     * 
     * @return current authentication state (safe copy)
     */
    public Map<String, String> getAuthenticationState() {
        return new HashMap<>(authenticationState);
    }

    /**
     * Update credentials for basic authentication
     * 
     * @param username username
     * @param password password
     */
    public void setBasicAuthentication(String username, String password) {
        if (username != null && password != null) {
            // Store credentials for WebDAV service
            authenticationState.put("username", username);
            authenticationState.put("password", password);
            
            // Also set Authorization header so hasAuthentication() returns true
            String credentials = username + ":" + password;
            String encodedCredentials = java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
            String authHeaderValue = "Basic " + encodedCredentials;
            sharedHeaders.put(AUTHORIZATION_HEADER, authHeaderValue);
            
            logger.debug("Set basic authentication credentials and Authorization header");
        }
    }

    /**
     * Get username for basic authentication
     * 
     * @return username or null
     */
    public String getUsername() {
        return authenticationState.get("username");
    }

    /**
     * Get password for basic authentication
     * 
     * @return password or null
     */
    public String getPassword() {
        return authenticationState.get("password");
    }

    /**
     * Refresh authentication by triggering re-authentication process
     * This method can be extended to implement automatic token refresh
     */
    public void refreshCredentials() {
        // Clear current authentication to force re-authentication
        String username = getUsername();
        String password = getPassword();
        
        clearAuthentication();
        
        // Restore basic credentials if available
        if (username != null && password != null) {
            setBasicAuthentication(username, password);
        }
        
        logger.info("Refreshed credentials - authentication state cleared");
    }
}