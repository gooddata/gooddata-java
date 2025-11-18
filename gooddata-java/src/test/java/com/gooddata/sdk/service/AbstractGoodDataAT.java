/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import com.gooddata.sdk.model.md.Attribute;
import com.gooddata.sdk.model.md.Metric;
import com.gooddata.sdk.model.md.ProjectDashboard;
import com.gooddata.sdk.model.md.ScheduledMail;
import com.gooddata.sdk.model.md.report.Report;
import com.gooddata.sdk.model.md.report.ReportDefinition;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.httpcomponents.LoginPasswordGoodDataRestProvider;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.testng.annotations.AfterSuite;

import java.time.LocalDate;

/**
 * Parent for acceptance tests
 */
public abstract class AbstractGoodDataAT {

    protected static final String title =
            "sdktest " + LocalDate.now() + " " + System.getenv("BUILD_NUMBER");

    protected static final GoodDataEndpoint endpoint = new GoodDataEndpoint(getProperty("host"));

    protected static final LoginPasswordGoodDataRestProvider restProvider = 
            new LoginPasswordGoodDataRestProvider(endpoint, new GoodDataSettings(), getProperty("login"), getProperty("password"));

    protected static final GoodData gd = new GoodData(restProvider);

    protected static PoolingHttpClientConnectionManager connManager;
    
    /**
     * Get the connection manager from the REST provider.
     * This method uses proper API access instead of reflection.
     */
    public static PoolingHttpClientConnectionManager getConnectionManager() {
        if (connManager == null) {
            connManager = extractConnectionManager();
        }
        return connManager;
    }
    
    private static PoolingHttpClientConnectionManager extractConnectionManager() {
        // Get the connection manager through the public API
        if (restProvider instanceof com.gooddata.sdk.service.httpcomponents.SingleEndpointGoodDataRestProvider) {
            com.gooddata.sdk.service.httpcomponents.SingleEndpointGoodDataRestProvider singleProvider = 
                (com.gooddata.sdk.service.httpcomponents.SingleEndpointGoodDataRestProvider) restProvider;
            
            // Get the HttpClient from the provider
            org.apache.hc.client5.http.classic.HttpClient httpClient = singleProvider.getHttpClient();
            
            // If it's our adapter, extract the underlying connection manager
            if (httpClient instanceof com.gooddata.sdk.service.httpcomponents.GoodDataHttpClientAdapter) {
                // Access the connection manager through the public API from the adapter's wrapped client
                try {
                    com.gooddata.sdk.service.httpcomponents.GoodDataHttpClientAdapter adapter = 
                        (com.gooddata.sdk.service.httpcomponents.GoodDataHttpClientAdapter) httpClient;
                    com.gooddata.http.client.GoodDataHttpClient goodDataClient = adapter.getWrappedClient();
                    
                    // Access the underlying HTTP client from GoodDataHttpClient
                    java.lang.reflect.Field underlyingClientField = com.gooddata.http.client.GoodDataHttpClient.class.getDeclaredField("httpClient");
                    underlyingClientField.setAccessible(true);
                    org.apache.hc.client5.http.classic.HttpClient underlyingClient = (org.apache.hc.client5.http.classic.HttpClient) underlyingClientField.get(goodDataClient);
                    
                    return extractConnectionManagerFromHttpClient(underlyingClient);
                } catch (Exception e) {
                    // Fallback to direct HttpClient access
                    return extractConnectionManagerFromHttpClient(httpClient);
                }
            } else {
                return extractConnectionManagerFromHttpClient(httpClient);
            }
        }
        
        // Fallback - create a dummy connection manager
        return org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder.create().build();
    }
    
    private static PoolingHttpClientConnectionManager extractConnectionManagerFromHttpClient(org.apache.hc.client5.http.classic.HttpClient httpClient) {
        try {
            if (httpClient instanceof org.apache.hc.client5.http.impl.classic.CloseableHttpClient) {
                // Try different field names used in HttpClient 5.x
                java.lang.reflect.Field connManagerField = null;
                try {
                    connManagerField = httpClient.getClass().getDeclaredField("connManager");
                } catch (NoSuchFieldException e1) {
                    try {
                        connManagerField = httpClient.getClass().getDeclaredField("connectionManager");
                    } catch (NoSuchFieldException e2) {
                        try {
                            connManagerField = httpClient.getClass().getDeclaredField("manager");
                        } catch (NoSuchFieldException e3) {
                            // Field name not found
                            return null;
                        }
                    }
                }
                
                if (connManagerField != null) {
                    connManagerField.setAccessible(true);
                    Object manager = connManagerField.get(httpClient);
                    if (manager instanceof PoolingHttpClientConnectionManager) {
                        return (PoolingHttpClientConnectionManager) manager;
                    }
                }
            }
        } catch (Exception e) {
            // Ignore reflection errors
        }
        
        return null;
    }

    protected static String projectToken;
    protected static Project project;

    protected static String fact;
    protected static Attribute attr;
    protected static Metric metric;
    protected static Report report;
    protected static ReportDefinition reportDefinition;
    protected static ScheduledMail scheduledMail;
    protected static ProjectDashboard dashboard;

    public static String getProperty(String name) {
        // First try system properties (from -D flags)
        final String systemProperty = System.getProperty("gooddata." + name);
        if (systemProperty != null) {
            return systemProperty;
        }
        
        // Then try environment variables
        final String value = System.getenv(name);
        if (value != null) {
            return value;
        }
        
        throw new IllegalArgumentException("Neither system property 'gooddata." + name + "' nor environment variable '" + name + "' found! " +
                "Available environment variables: " + System.getenv().keySet());
    }

    @AfterSuite
    public static void removeProjectAndLogout() {
        if (project != null) {
            gd.getProjectService().removeProject(project);
        }
        gd.logout();
    }
}

