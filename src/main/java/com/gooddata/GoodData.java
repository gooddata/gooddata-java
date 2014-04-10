/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import com.gooddata.account.AccountService;
import com.gooddata.http.client.GoodDataHttpClient;
import com.gooddata.http.client.LoginSSTRetrievalStrategy;
import com.gooddata.http.client.SSTRetrievalStrategy;
import com.gooddata.model.ModelService;
import com.gooddata.project.ProjectService;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
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

    private final RestTemplate restTemplate;

    public GoodData(String login, String password) {
        final String hostname = "staging.getgooddata.com";
        final int port = 443;
        final String protocol = "https";
        HttpHost host = new HttpHost(hostname, port, protocol);

        SSTRetrievalStrategy strategy = new LoginSSTRetrievalStrategy(HttpClientBuilder.create().build(), host, login, password);

        HttpClient client = new GoodDataHttpClient(HttpClientBuilder.create().build(), strategy);


        final UriPrefixingClientHttpRequestFactory factory = new UriPrefixingClientHttpRequestFactory(
                new HttpComponentsClientHttpRequestFactory(client), hostname, port, protocol);
        restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Arrays.<ClientHttpRequestInterceptor>asList(
                new HeaderAddingRequestInterceptor(singletonMap("Accept", MediaType.APPLICATION_JSON_VALUE))));
    }

    public ProjectService getProjectService() {
       return new ProjectService(restTemplate, getAccountService());
    }

    public AccountService getAccountService() {
        return new AccountService(restTemplate);
    }

    public void logout() {
        getAccountService().logout();
    }

    public ModelService getModelService() {
        // TODO do not return always new
        return new ModelService(restTemplate);
    }
}
