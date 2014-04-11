/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import com.gooddata.account.AccountService;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.gdc.GdcService;
import com.gooddata.http.client.GoodDataHttpClient;
import com.gooddata.http.client.LoginSSTRetrievalStrategy;
import com.gooddata.http.client.SSTRetrievalStrategy;
import com.gooddata.md.MetadataService;
import com.gooddata.model.ModelService;
import com.gooddata.project.ProjectService;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static java.util.Collections.singletonMap;

/**
 */
public class GoodData {

    private static final String PROTOCOL = "https";
    private static final int PORT = 443;
    private static final String HOSTNAME = "secure.gooddata.com";

    private final RestTemplate restTemplate;
    private final HttpClientBuilder httpClientBuilder;
    private final String login;
    private final String password;

    public GoodData(String login, String password) {
        this(login, password, HOSTNAME);
    }

    public GoodData(String hostname, String login, String password) {
        this.login = login;
        this.password = password;
        final HttpHost host = new HttpHost(hostname, PORT, PROTOCOL);

        httpClientBuilder = HttpClientBuilder.create();
        final CloseableHttpClient httpClient = httpClientBuilder.build();
        final SSTRetrievalStrategy strategy = new LoginSSTRetrievalStrategy(httpClient, host, login, password);

        final HttpClient client = new GoodDataHttpClient(httpClient, strategy);

        final UriPrefixingClientHttpRequestFactory factory = new UriPrefixingClientHttpRequestFactory(
                new HttpComponentsClientHttpRequestFactory(client), hostname, PORT, PROTOCOL);
        restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Arrays.<ClientHttpRequestInterceptor>asList(
                new HeaderAddingRequestInterceptor(singletonMap("Accept", MediaType.APPLICATION_JSON_VALUE))));
    }

    public void logout() {
        getAccountService().logout();
    }

    public ProjectService getProjectService() {
       return new ProjectService(restTemplate, getAccountService());
    }

    public AccountService getAccountService() {
        return new AccountService(restTemplate);
    }

    public MetadataService getMetadataService() {
        return new MetadataService(restTemplate);
    }

    public ModelService getModelService() {
        return new ModelService(restTemplate);
    }

    public GdcService getGdcService() {
        return new GdcService(restTemplate);
    }

    public DataStoreService getDataStoreService() {
        return new DataStoreService(httpClientBuilder, getGdcService(), login, password);
    }
}
