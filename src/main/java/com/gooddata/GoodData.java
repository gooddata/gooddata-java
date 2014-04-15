/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import com.gooddata.account.AccountService;
import com.gooddata.dataset.DatasetService;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.gdc.GdcService;
import com.gooddata.http.client.GoodDataHttpClient;
import com.gooddata.http.client.LoginSSTRetrievalStrategy;
import com.gooddata.http.client.SSTRetrievalStrategy;
import com.gooddata.md.MetadataService;
import com.gooddata.model.ModelService;
import com.gooddata.project.ProjectService;
import com.gooddata.report.ReportService;
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

    private final AccountService accountService;
    private final ProjectService projectService;
    private final MetadataService metadataService;
    private final ModelService modelService;
    private final GdcService gdcService;
    private final DataStoreService dataStoreService;
    private final DatasetService datasetService;
    private final ReportService reportService;

    public GoodData(String login, String password) {
        this(HOSTNAME, login, password);
    }

    public GoodData(String hostname, String login, String password) {
        final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        final RestTemplate restTemplate = createRestTemplate(hostname, login, password, httpClientBuilder);

        accountService = new AccountService(restTemplate);
        projectService = new ProjectService(restTemplate, accountService);
        metadataService = new MetadataService(restTemplate);
        modelService = new ModelService(restTemplate);
        gdcService = new GdcService(restTemplate);
        dataStoreService = new DataStoreService(httpClientBuilder, gdcService, login, password);
        datasetService = new DatasetService(restTemplate, dataStoreService);
        reportService = new ReportService(restTemplate);
    }

    private RestTemplate createRestTemplate(String hostname, String login, String password, HttpClientBuilder httpClientBuilder) {
        final HttpHost host = new HttpHost(hostname, PORT, PROTOCOL);
        final CloseableHttpClient httpClient = httpClientBuilder.build();
        final SSTRetrievalStrategy strategy = new LoginSSTRetrievalStrategy(httpClient, host, login, password);

        final HttpClient client = new GoodDataHttpClient(httpClient, strategy);

        final UriPrefixingClientHttpRequestFactory factory = new UriPrefixingClientHttpRequestFactory(
                new HttpComponentsClientHttpRequestFactory(client), hostname, PORT, PROTOCOL);
        final RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Arrays.<ClientHttpRequestInterceptor>asList(
                new HeaderSettingRequestInterceptor(singletonMap("Accept", MediaType.APPLICATION_JSON_VALUE))));
        return restTemplate;
    }

    public void logout() {
        getAccountService().logout();
    }

    public ProjectService getProjectService() {
        return projectService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public MetadataService getMetadataService() {
        return metadataService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public GdcService getGdcService() {
        return gdcService;
    }

    public DataStoreService getDataStoreService() {
        return dataStoreService;
    }

    public DatasetService getDatasetService() {
        return datasetService;
    }

    public ReportService getReportService() {
        return reportService;
    }

}
