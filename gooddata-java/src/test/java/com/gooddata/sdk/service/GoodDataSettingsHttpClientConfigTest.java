/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class GoodDataSettingsHttpClientConfigTest {

    private GoodDataSettings settings;

    @BeforeMethod
    public void setUp() {
        settings = new GoodDataSettings();
    }

    @Test
    public void testDefaultHttpClientConfig() {
        // When
        GoodDataSettings.HttpClientConfig config = settings.getHttpClientConfig();

        // Then
        assertNotNull(config);
        assertEquals(config.getMaxConnections(), 20);
        assertEquals(config.getMaxConnectionsPerRoute(), 10);
        assertEquals(config.getConnectionTimeoutMs(), 10000);
        assertEquals(config.getSocketTimeoutMs(), 60000);
        assertEquals(config.getConnectionRequestTimeoutMs(), 10000);
        assertTrue(config.isEnableCookies());
        assertEquals(config.getCookieSpec(), "strict");
    }

    @Test
    public void testSetCustomHttpClientConfig() {
        // Given
        GoodDataSettings.HttpClientConfig customConfig = new GoodDataSettings.HttpClientConfig();
        customConfig.setMaxConnections(50);
        customConfig.setMaxConnectionsPerRoute(25);
        customConfig.setConnectionTimeoutMs(5000);
        customConfig.setSocketTimeoutMs(30000);
        customConfig.setConnectionRequestTimeoutMs(2000);
        customConfig.setEnableCookies(false);
        customConfig.setCookieSpec("relaxed");

        // When
        settings.setHttpClientConfig(customConfig);

        // Then
        GoodDataSettings.HttpClientConfig retrievedConfig = settings.getHttpClientConfig();
        assertEquals(retrievedConfig.getMaxConnections(), 50);
        assertEquals(retrievedConfig.getMaxConnectionsPerRoute(), 25);
        assertEquals(retrievedConfig.getConnectionTimeoutMs(), 5000);
        assertEquals(retrievedConfig.getSocketTimeoutMs(), 30000);
        assertEquals(retrievedConfig.getConnectionRequestTimeoutMs(), 2000);
        assertFalse(retrievedConfig.isEnableCookies());
        assertEquals(retrievedConfig.getCookieSpec(), "relaxed");
    }

    @Test
    public void testHttpClientConfigValidation() {
        // Given
        GoodDataSettings.HttpClientConfig config = new GoodDataSettings.HttpClientConfig();

        // Test valid values
        config.setMaxConnections(1);
        config.setMaxConnectionsPerRoute(1);
        config.setConnectionTimeoutMs(0);
        config.setSocketTimeoutMs(0);
        config.setConnectionRequestTimeoutMs(0);

        // Should not throw exceptions
        assertEquals(config.getMaxConnections(), 1);
        assertEquals(config.getMaxConnectionsPerRoute(), 1);
        assertEquals(config.getConnectionTimeoutMs(), 0);
        assertEquals(config.getSocketTimeoutMs(), 0);
        assertEquals(config.getConnectionRequestTimeoutMs(), 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testHttpClientConfigValidationMaxConnectionsZero() {
        GoodDataSettings.HttpClientConfig config = new GoodDataSettings.HttpClientConfig();
        config.setMaxConnections(0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testHttpClientConfigValidationMaxConnectionsNegative() {
        GoodDataSettings.HttpClientConfig config = new GoodDataSettings.HttpClientConfig();
        config.setMaxConnections(-1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testHttpClientConfigValidationMaxConnectionsPerRouteZero() {
        GoodDataSettings.HttpClientConfig config = new GoodDataSettings.HttpClientConfig();
        config.setMaxConnectionsPerRoute(0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testHttpClientConfigValidationConnectionTimeoutNegative() {
        GoodDataSettings.HttpClientConfig config = new GoodDataSettings.HttpClientConfig();
        config.setConnectionTimeoutMs(-1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testHttpClientConfigValidationSocketTimeoutNegative() {
        GoodDataSettings.HttpClientConfig config = new GoodDataSettings.HttpClientConfig();
        config.setSocketTimeoutMs(-1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testHttpClientConfigValidationConnectionRequestTimeoutNegative() {
        GoodDataSettings.HttpClientConfig config = new GoodDataSettings.HttpClientConfig();
        config.setConnectionRequestTimeoutMs(-1);
    }

    @Test
    public void testGoodDataSettingsEqualsAndHashCodeWithHttpClientConfig() {
        // Given
        GoodDataSettings settings1 = new GoodDataSettings();
        GoodDataSettings settings2 = new GoodDataSettings();

        // Initially equal
        assertEquals(settings1, settings2);
        assertEquals(settings1.hashCode(), settings2.hashCode());

        // Modify one with custom config
        GoodDataSettings.HttpClientConfig customConfig = new GoodDataSettings.HttpClientConfig();
        customConfig.setMaxConnections(100);
        settings1.setHttpClientConfig(customConfig);

        // Should not be equal anymore
        assertNotEquals(settings1, settings2);
        assertNotEquals(settings1.hashCode(), settings2.hashCode());

        // Set the same config in settings2
        GoodDataSettings.HttpClientConfig customConfig2 = new GoodDataSettings.HttpClientConfig();
        customConfig2.setMaxConnections(100);
        settings2.setHttpClientConfig(customConfig2);

        // Should be equal again
        assertEquals(settings1, settings2);
        assertEquals(settings1.hashCode(), settings2.hashCode());
    }

    @Test
    public void testHttpClientConfigCreatedOnFirstAccess() {
        // Given
        GoodDataSettings freshSettings = new GoodDataSettings();

        // When
        GoodDataSettings.HttpClientConfig config1 = freshSettings.getHttpClientConfig();
        GoodDataSettings.HttpClientConfig config2 = freshSettings.getHttpClientConfig();

        // Then
        assertNotNull(config1);
        assertSame(config1, config2); // Should return the same instance
    }
}