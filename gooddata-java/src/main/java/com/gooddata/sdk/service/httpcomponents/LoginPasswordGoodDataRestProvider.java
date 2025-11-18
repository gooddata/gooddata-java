/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import com.gooddata.http.client.GoodDataHttpClient;
import com.gooddata.http.client.LoginSSTRetrievalStrategy;
import com.gooddata.http.client.SSTRetrievalStrategy;
import com.gooddata.sdk.service.GoodDataEndpoint;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.service.gdc.DataStoreService;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * The default {@link com.gooddata.sdk.service.GoodDataRestProvider} used internally by {@link com.gooddata.sdk.service.GoodData}.
 * Provides configured single endpoint REST connection using standard GoodData login and password authentication.
 *
 * See https://help.gooddata.com/display/API/API+Reference#/reference/authentication/log-in
 */
public final class LoginPasswordGoodDataRestProvider extends SingleEndpointGoodDataRestProvider {

    private static final Logger logger = LoggerFactory.getLogger(LoginPasswordGoodDataRestProvider.class);
    
    private final String login;
    private final String password;

    /**
     * Creates new instance.
     * @param endpoint endpoint of GoodData API
     * @param settings settings
     * @param login API user login
     * @param password API user password
     */
    public LoginPasswordGoodDataRestProvider(final GoodDataEndpoint endpoint, final GoodDataSettings settings,
                                             final String login, final String password) {
        super(endpoint, settings, (builder, builderEndpoint, builderSettings) -> createHttpClient(builder, builderEndpoint, login, password));
        this.login = login;
        this.password = password;
    }

    /**
     * Creates http client using given builder and endpoint, authenticating by login and password.
     * @param builder builder to build client from
     * @param endpoint API endpoint to connect client to
     * @param login login
     * @param password password
     * @return configured http client
     */
    public static HttpClient createHttpClient(final HttpClientBuilder builder, final GoodDataEndpoint endpoint,
                                              final String login, final String password) {
        notNull(endpoint, "endpoint");
        notNull(builder, "builder");
        notNull(login, "login");
        notNull(password, "password");

        final HttpClient httpClient = builder.build();
        final SSTRetrievalStrategy strategy = new LoginSSTRetrievalStrategy(login, password);
        final HttpHost httpHost = new HttpHost(endpoint.getProtocol(), endpoint.getHostname(), endpoint.getPort());
        final GoodDataHttpClient goodDataClient = new GoodDataHttpClient(httpClient, httpHost, strategy);
        return new GoodDataHttpClientAdapter(goodDataClient);
    }

    /**
     * Get the login used for authentication
     * @return login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Get the password used for authentication  
     * @return password
     */
    public String getPassword() {
        return password;
    }

    @Override
    public Optional<DataStoreService> getDataStoreService(Supplier<String> stagingUriSupplier) {
        try {
            Class.forName("com.gooddata.webdav.WebDavService", false, getClass().getClassLoader());
            // Create WebDAV service with credentials from RestTemplate
            com.gooddata.webdav.WebDavService webDavService = new com.gooddata.webdav.SardineWebDavService();
            
            // Create credential manager to share auth between RestTemplate and WebDAV
            com.gooddata.sdk.service.auth.CredentialManager credentialManager = 
                new com.gooddata.sdk.service.auth.CredentialManager();
            
            // Initialize credential manager with login credentials
            credentialManager.setBasicAuthentication(login, password);
            
            return Optional.of(new DataStoreService(this, stagingUriSupplier, webDavService, credentialManager));
        } catch (ClassNotFoundException e) {
            logger.info("Optional dependency gooddata-webdav-service not found - WebDAV related operations are not supported");
            return Optional.empty();
        }
    }
}

