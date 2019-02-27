/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import com.gooddata.UriPrefixingClientHttpRequestFactory;
import com.gooddata.sdk.service.account.AccountService;
import com.gooddata.sdk.service.auditevent.AuditEventService;
import com.gooddata.sdk.service.connector.ConnectorService;
import com.gooddata.sdk.service.dataload.OutputStageService;
import com.gooddata.sdk.service.dataload.processes.ProcessService;
import com.gooddata.sdk.service.executeafm.ExecuteAfmService;
import com.gooddata.sdk.service.export.ExportService;
import com.gooddata.sdk.service.featureflag.FeatureFlagService;
import com.gooddata.gdc.Header;
import com.gooddata.sdk.service.lcm.LcmService;
import com.gooddata.sdk.service.md.maintenance.ExportImportService;
import com.gooddata.sdk.service.notification.NotificationService;
import com.gooddata.sdk.service.projecttemplate.ProjectTemplateService;
import com.gooddata.sdk.service.util.ResponseErrorHandler;
import com.gooddata.sdk.service.authentication.LoginPasswordAuthentication;
import com.gooddata.sdk.service.warehouse.WarehouseService;
import com.gooddata.sdk.service.dataset.DatasetService;
import com.gooddata.sdk.service.gdc.DataStoreService;
import com.gooddata.sdk.service.gdc.GdcService;
import com.gooddata.sdk.service.md.MetadataService;
import com.gooddata.sdk.service.project.model.ModelService;
import com.gooddata.sdk.service.project.ProjectService;
import com.gooddata.sdk.service.report.ReportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.VersionInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static com.gooddata.util.Validate.notNull;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.apache.http.util.VersionInfo.loadVersionInfo;

/**
 * Entry point for GoodData SDK usage.
 * <p>
 * Configure connection to GoodData using one of constructors. One can then get initialized service he needs from
 * the newly constructed instance. This instance can be also used later for logout from GoodData Platform.
 * <p>
 * Usage example:
 * <pre><code>
 *     GoodData gd = new GoodData("roman@gooddata.com", "Roman1");
 *     // do something useful like: gd.getSomeService().doSomething()
 *     gd.logout();
 * </code></pre>
 */
public class GoodData {

    /**
     * @deprecated use {@link Header#GDC_REQUEST_ID} instead.
     */
    @Deprecated
    public static final String GDC_REQUEST_ID_HEADER = Header.GDC_REQUEST_ID;

    protected static final String PROTOCOL = GoodDataEndpoint.PROTOCOL;
    protected static final int PORT = GoodDataEndpoint.PORT;
    protected static final String HOSTNAME = GoodDataEndpoint.HOSTNAME;
    private static final String UNKNOWN_VERSION = "UNKNOWN";

    private final RestTemplate restTemplate;
    private final HttpClient httpClient;
    private final AccountService accountService;
    private final ProjectService projectService;
    private final MetadataService metadataService;
    private final ModelService modelService;
    private final GdcService gdcService;
    private final DataStoreService dataStoreService;
    private final DatasetService datasetService;
    @SuppressWarnings("deprecation")
    private final ReportService reportService;
    private final ConnectorService connectorService;
    private final ProcessService processService;
    private final WarehouseService warehouseService;
    private final NotificationService notificationService;
    private final ExportImportService exportImportService;
    private final FeatureFlagService featureFlagService;
    private final OutputStageService outputStageService;
    private final ProjectTemplateService projectTemplateService;
    private final ExportService exportService;
    private final AuditEventService auditEventService;
    private final ExecuteAfmService executeAfmService;
    private final LcmService lcmService;

    /**
     * Create instance configured to communicate with GoodData Platform under user with given credentials.
     *
     * @param login    GoodData user's login
     * @param password GoodData user's password
     */
    public GoodData(String login, String password) {
        this(HOSTNAME, login, password, new GoodDataSettings());
    }

    /**
     * Create instance configured to communicate with GoodData Platform under user with given credentials.
     *
     * @param login    GoodData user's login
     * @param password GoodData user's password
     * @param settings additional settings
     */
    public GoodData(String login, String password, GoodDataSettings settings) {
        this(HOSTNAME, login, password, settings);
    }

    /**
     * Create instance configured to communicate with GoodData Platform running on given host using given user's
     * credentials.
     *
     * @param hostname GoodData Platform's host name (e.g. secure.gooddata.com)
     * @param login    GoodData user's login
     * @param password GoodData user's password
     */
    public GoodData(String hostname, String login, String password) {
        this(hostname, login, password, PORT, PROTOCOL, new GoodDataSettings());
    }

    /**
     * Create instance configured to communicate with GoodData Platform running on given host using given user's
     * credentials.
     *
     * @param hostname GoodData Platform's host name (e.g. secure.gooddata.com)
     * @param login    GoodData user's login
     * @param password GoodData user's password
     * @param settings additional settings
     */
    public GoodData(String hostname, String login, String password, GoodDataSettings settings) {
        this(hostname, login, password, PORT, PROTOCOL, settings);
    }

    /**
     * Create instance configured to communicate with GoodData Platform running on given host and port using given user's
     * credentials.
     *
     * @param hostname GoodData Platform's host name (e.g. secure.gooddata.com)
     * @param login    GoodData user's login
     * @param password GoodData user's password
     * @param port     GoodData Platform's API port (e.g. 443)
     */
    public GoodData(String hostname, String login, String password, int port) {
        this(hostname, login, password, port, PROTOCOL, new GoodDataSettings());
    }

    /**
     * Create instance configured to communicate with GoodData Platform running on given host and port using given user's
     * credentials.
     *
     * @param hostname GoodData Platform's host name (e.g. secure.gooddata.com)
     * @param login    GoodData user's login
     * @param password GoodData user's password
     * @param port     GoodData Platform's API port (e.g. 443)
     * @param settings additional settings
     */
    public GoodData(String hostname, String login, String password, int port, GoodDataSettings settings) {
        this(hostname, login, password, port, PROTOCOL, settings);
    }

    /**
     * Create instance configured to communicate with GoodData Platform running on given host, port and protocol using
     * given user's credentials.
     *
     * @param hostname GoodData Platform's host name (e.g. secure.gooddata.com)
     * @param login    GoodData user's login
     * @param password GoodData user's password
     * @param port     GoodData Platform's API port (e.g. 443)
     * @param protocol GoodData Platform's API protocol (e.g. https)
     * @param settings additional settings
     */
    protected GoodData(String hostname, String login, String password, int port, String protocol, GoodDataSettings settings) {
        this(
                new GoodDataEndpoint(hostname, port, protocol),
                new LoginPasswordAuthentication(login, password),
                settings
        );
    }

    /**
     * Create instance configured to communicate with GoodData Platform running on given endpoint and using
     * given http client factory.
     *
     * @param endpoint GoodData Platform's endpoint
     * @param authentication authentication
     */
    protected GoodData(GoodDataEndpoint endpoint, Authentication authentication) {
        this(endpoint, authentication, new GoodDataSettings());
    }

    /**
     * Create instance configured to communicate with GoodData Platform running on given endpoint and using
     * given http client factory.
     *
     * @param endpoint GoodData Platform's endpoint
     * @param authentication authentication
     * @param settings additional settings
     */
    @SuppressWarnings("deprecation")
    protected GoodData(GoodDataEndpoint endpoint, Authentication authentication, GoodDataSettings settings) {
        httpClient = authentication.createHttpClient(endpoint, createHttpClientBuilder(settings));

        restTemplate = createRestTemplate(endpoint, httpClient);

        accountService = new AccountService(getRestTemplate(), settings);
        projectService = new ProjectService(getRestTemplate(), accountService, settings);
        metadataService = new MetadataService(getRestTemplate(), settings);
        modelService = new ModelService(getRestTemplate(), settings);
        gdcService = new GdcService(getRestTemplate(), settings);
        dataStoreService = new DataStoreService(getHttpClient(), getRestTemplate(), gdcService, endpoint.toUri());
        datasetService = new DatasetService(getRestTemplate(), dataStoreService, settings);
        exportService = new ExportService(getRestTemplate(), endpoint, settings);
        reportService = new ReportService(exportService, getRestTemplate(), settings);
        processService = new ProcessService(getRestTemplate(), accountService, dataStoreService, settings);
        warehouseService = new WarehouseService(getRestTemplate(), settings);
        connectorService = new ConnectorService(getRestTemplate(), projectService, settings);
        notificationService = new NotificationService(getRestTemplate(), settings);
        exportImportService = new ExportImportService(getRestTemplate(), settings);
        featureFlagService = new FeatureFlagService(getRestTemplate(), settings);
        outputStageService = new OutputStageService(getRestTemplate(), settings);
        projectTemplateService = new ProjectTemplateService(getRestTemplate(), settings);
        auditEventService = new AuditEventService(getRestTemplate(), accountService, settings);
        executeAfmService = new ExecuteAfmService(getRestTemplate(), settings);
        lcmService = new LcmService(getRestTemplate(), settings);
    }

    static RestTemplate createRestTemplate(GoodDataEndpoint endpoint, HttpClient httpClient) {
        notNull(endpoint, "endpoint");
        notNull(httpClient, "httpClient");

        final UriPrefixingClientHttpRequestFactory factory = new UriPrefixingClientHttpRequestFactory(
                new HttpComponentsClientHttpRequestFactory(httpClient),
                endpoint.toUri()
        );

        final Map<String, String> presetHeaders = new HashMap<>(2);
        presetHeaders.put("Accept", MediaType.APPLICATION_JSON_VALUE);
        presetHeaders.put(Header.GDC_VERSION, readApiVersion());

        final RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(asList(
                new HeaderSettingRequestInterceptor(presetHeaders),
                new DeprecationWarningRequestInterceptor()));

        restTemplate.setErrorHandler(new ResponseErrorHandler(restTemplate.getMessageConverters()));

        return restTemplate;
    }

    private HttpClientBuilder createHttpClientBuilder(final GoodDataSettings settings) {
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(settings.getMaxConnections());
        connectionManager.setMaxTotal(settings.getMaxConnections());

        final SocketConfig.Builder socketConfig = SocketConfig.copy(SocketConfig.DEFAULT);
        socketConfig.setSoTimeout(settings.getSocketTimeout());
        connectionManager.setDefaultSocketConfig(socketConfig.build());

        final RequestConfig.Builder requestConfig = RequestConfig.copy(RequestConfig.DEFAULT);
        requestConfig.setConnectTimeout(settings.getConnectionTimeout());
        requestConfig.setConnectionRequestTimeout(settings.getConnectionRequestTimeout());
        requestConfig.setSocketTimeout(settings.getSocketTimeout());

        return HttpClientBuilder.create()
                .setUserAgent(StringUtils.isNotBlank(settings.getUserAgent()) ? String.format("%s %s", settings.getUserAgent(), getUserAgent()) : getUserAgent())
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig.build());
    }

    private String getUserAgent() {
        final Package pkg = Package.getPackage("com.gooddata");
        final String clientVersion = pkg != null && pkg.getImplementationVersion() != null
                ? pkg.getImplementationVersion() : UNKNOWN_VERSION;

        final VersionInfo vi = loadVersionInfo("org.apache.http.client", HttpClientBuilder.class.getClassLoader());
        final String apacheVersion = vi != null ? vi.getRelease() : UNKNOWN_VERSION;

        return String.format("%s/%s (%s; %s) %s/%s", "GoodData-Java-SDK", clientVersion,
                System.getProperty("os.name"), System.getProperty("java.specification.version"),
                "Apache-HttpClient", apacheVersion);
    }

    private static String readApiVersion() {
        try {
            return StreamUtils.copyToString(GoodData.class.getResourceAsStream("/GoodDataApiVersion"), Charset.defaultCharset());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read GoodDataApiVersion from classpath", e);
        }
    }

    /**
     * Get the configured {@link RestTemplate} used by the library.
     * This is the extension point for inheriting classes providing additional services.
     * @return REST template
     */
    protected final RestTemplate getRestTemplate() {
        return restTemplate;
    }

    /**
     * Get the configured {@link HttpClient} used by the library.
     * This is the extension point for inheriting classes providing additional services.
     * @return HTTP client
     */
    protected final HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Logout from GoodData Platform
     */
    public void logout() {
        getAccountService().logout();
    }

    /**
     * Get initialized service for project management (to list projects, create a project, ...)
     *
     * @return initialized service for project management
     */
    @Bean("goodDataProjectService")
    public ProjectService getProjectService() {
        return projectService;
    }

    /**
     * Get initialized service for account management (to get current account information, logout, ...)
     *
     * @return initialized service for account management
     */
    @Bean("goodDataAccountService")
    public AccountService getAccountService() {
        return accountService;
    }

    /**
     * Get initialized service for metadata management (to query, create and update project metadata like attributes,
     * facts, metrics, reports, ...)
     *
     * @return initialized service for metadata management
     */
    @Bean("goodDataMetadataService")
    public MetadataService getMetadataService() {
        return metadataService;
    }

    /**
     * Get initialized service for model management (to get model diff, update model, ...)
     *
     * @return initialized service for model management
     */
    @Bean("goodDataModelService")
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * Get initialized service for API root management (to get API root links, ...)
     *
     * @return initialized service for API root management
     */
    @Bean("goodDataGdcService")
    public GdcService getGdcService() {
        return gdcService;
    }

    /**
     * Get initialized service for data store (user staging/WebDAV) management (to upload, download, delete, ...)
     *
     * @return initialized service for data store management
     */
    @Bean("goodDataDataStoreService")
    public DataStoreService getDataStoreService() {
        return dataStoreService;
    }

    /**
     * Get initialized service for dataset management (to list manifest, get datasets, load dataset, ...)
     *
     * @return initialized service for dataset management
     */
    @Bean("goodDataDatasetService")
    public DatasetService getDatasetService() {
        return datasetService;
    }

    /**
     * Get initialized service for report management (to execute and export report, ...)
     *
     * @return initialized service for report management
     */
    @Bean("goodDataReportService")
    @SuppressWarnings("deprecation")
    public ReportService getReportService() {
        return reportService;
    }

    /**
     * Get initialized service for exports management (export report,...)
     *
     * @return initialized service for exports
     */
    @Bean("goodDataExportService")
    public ExportService getExportService() {
        return exportService;
    }

    /**
     * Get initialized service for dataload processes management and process executions.
     *
     * @return initialized service for dataload processes management and process executions
     */
    @Bean("goodDataProcessService")
    public ProcessService getProcessService() {
        return processService;
    }

    /**
     * Get initialized service for ADS management (create, access and delete ads instances).
     *
     * @return initialized service for ADS management
     */
    @Bean("goodDataWarehouseService")
    public WarehouseService getWarehouseService() {
        return warehouseService;
    }

    /**
     * Get initialized service for connector integration management (create, update, start process, ...).
     *
     * @return initialized service for connector integration management
     */
    @Bean("goodDataConnectorService")
    public ConnectorService getConnectorService() {
        return connectorService;
    }

    /**
     * Get initialized service for project notifications management.
     *
     * @return initialized service for project notifications management
     */
    @Bean("goodDataNotificationService")
    public NotificationService getNotificationService() {
        return notificationService;
    }

    /**
     * Get initialized service for metadata export/import.
     *
     * @return initialized service for metadata export/import
     */
    @Bean("goodDataExportImportService")
    public ExportImportService getExportImportService() {
        return exportImportService;
    }

    /**
     * Get initialized service for feature flag management.
     *
     * @return initialized service for feature flag management
     */
    @Bean("goodDataFeatureFlagService")
    public FeatureFlagService getFeatureFlagService() {
        return featureFlagService;
    }

    /**
     * Get initialized service for output stage management.
     *
     * @return initialized service for output stage management
     */
    @Bean("goodDataOutputStageService")
    public OutputStageService getOutputStageService() {
        return outputStageService;
    }

    /**
     * Get initialized service for project templates
     *
     * @return initialized service for project templates
     */
    @Bean("goodDataProjectTemplateService")
    public ProjectTemplateService getProjectTemplateService() {
        return projectTemplateService;
    }

    /**
     * Get initialized service for audit events
     * @return initialized service for audit events
     */
    @Bean("goodDataAuditEventService")
    public AuditEventService getAuditEventService() {
        return auditEventService;
    }

    /**
     * Get initialized service for afm execution
     * @return initialized service for afm execution
     */
    @Bean("goodDataExecuteAfmService")
    public ExecuteAfmService getExecuteAfmService() {
        return executeAfmService;
    }

    /**
     * Get initialized service for Life Cycle Management
     * @return initialized service for Life Cycle Management
     */
    @Bean("goodDataLcmService")
    public LcmService getLcmService() {
        return lcmService;
    }
}
