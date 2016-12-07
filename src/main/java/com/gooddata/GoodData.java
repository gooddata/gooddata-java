/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata;

import com.gooddata.account.AccountService;
import com.gooddata.connector.ConnectorService;
import com.gooddata.dataload.OutputStageService;
import com.gooddata.dataload.processes.ProcessService;
import com.gooddata.featureflag.FeatureFlagService;
import com.gooddata.md.maintenance.ExportImportService;
import com.gooddata.notification.NotificationService;
import com.gooddata.util.ResponseErrorHandler;
import com.gooddata.authentication.LoginPasswordAuthentication;
import com.gooddata.warehouse.WarehouseService;
import com.gooddata.dataset.DatasetService;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.gdc.GdcService;
import com.gooddata.md.MetadataService;
import com.gooddata.model.ModelService;
import com.gooddata.project.ProjectService;
import com.gooddata.report.ReportService;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.VersionInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static com.gooddata.util.Validate.notNull;
import static java.util.Collections.singletonMap;
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

    public static final String GDC_REQUEST_ID_HEADER = "X-GDC-REQUEST";

    protected static final String PROTOCOL = GoodDataEndpoint.PROTOCOL;
    protected static final int PORT = GoodDataEndpoint.PORT;
    protected static final String HOSTNAME = GoodDataEndpoint.HOSTNAME;
    private static final String UNKNOWN_VERSION = "UNKNOWN";

    private static final int RESTAPI_VERSION = 1;

    private final RestTemplate restTemplate;
    private final HttpClient httpClient;
    private final AccountService accountService;
    private final ProjectService projectService;
    private final MetadataService metadataService;
    private final ModelService modelService;
    private final GdcService gdcService;
    private final DataStoreService dataStoreService;
    private final DatasetService datasetService;
    private final ReportService reportService;
    private final ConnectorService connectorService;
    private final ProcessService processService;
    private final WarehouseService warehouseService;
    private final NotificationService notificationService;
    private final ExportImportService exportImportService;
    private final FeatureFlagService featureFlagService;
    private final OutputStageService outputStageService;

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
    protected GoodData(GoodDataEndpoint endpoint, Authentication authentication, GoodDataSettings settings) {
        httpClient = authentication.createHttpClient(endpoint, createHttpClientBuilder(settings));

        restTemplate = createRestTemplate(endpoint, httpClient);

        accountService = new AccountService(getRestTemplate());
        projectService = new ProjectService(getRestTemplate(), accountService);
        metadataService = new MetadataService(getRestTemplate());
        modelService = new ModelService(getRestTemplate());
        gdcService = new GdcService(getRestTemplate());
        dataStoreService = new DataStoreService(getHttpClient(), getRestTemplate(), gdcService, endpoint.toUri());
        datasetService = new DatasetService(getRestTemplate(), dataStoreService);
        reportService = new ReportService(getRestTemplate());
        processService = new ProcessService(getRestTemplate(), accountService, dataStoreService);
        warehouseService = new WarehouseService(getRestTemplate());
        connectorService = new ConnectorService(getRestTemplate(), projectService);
        notificationService = new NotificationService(getRestTemplate());
        exportImportService = new ExportImportService(getRestTemplate());
        featureFlagService = new FeatureFlagService(restTemplate);
        outputStageService = new OutputStageService(restTemplate);
    }

    static RestTemplate createRestTemplate(GoodDataEndpoint endpoint, HttpClient httpClient) {
        notNull(endpoint, "endpoint");
        notNull(httpClient, "httpClient");

        final UriPrefixingClientHttpRequestFactory factory = new UriPrefixingClientHttpRequestFactory(
                new HttpComponentsClientHttpRequestFactory(httpClient),
                endpoint.getHostname(),
                endpoint.getPort(),
                endpoint.getProtocol()
        );
        final RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Arrays.asList(
                new HeaderSettingRequestInterceptor(singletonMap("Accept", getAcceptHeaderValue()))));

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
                .setUserAgent(getUserAgent())
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig.build());
    }

    /*
     * Set accept header (application/json by default) and append rest api versioning information which is mandatory
     * for some resources.
     */
    private static String getAcceptHeaderValue(){
        return MediaType.APPLICATION_JSON_VALUE + ";version=" + RESTAPI_VERSION;
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
    @Bean
    public ProjectService getProjectService() {
        return projectService;
    }

    /**
     * Get initialized service for account management (to get current account information, logout, ...)
     *
     * @return initialized service for account management
     */
    @Bean
    public AccountService getAccountService() {
        return accountService;
    }

    /**
     * Get initialized service for metadata management (to query, create and update project metadata like attributes,
     * facts, metrics, reports, ...)
     *
     * @return initialized service for metadata management
     */
    @Bean
    public MetadataService getMetadataService() {
        return metadataService;
    }

    /**
     * Get initialized service for model management (to get model diff, update model, ...)
     *
     * @return initialized service for model management
     */
    @Bean
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * Get initialized service for API root management (to get API root links, ...)
     *
     * @return initialized service for API root management
     */
    @Bean
    public GdcService getGdcService() {
        return gdcService;
    }

    /**
     * Get initialized service for data store (user staging/WebDAV) management (to upload, download, delete, ...)
     *
     * @return initialized service for data store management
     */
    @Bean
    public DataStoreService getDataStoreService() {
        return dataStoreService;
    }

    /**
     * Get initialized service for dataset management (to list manifest, get datasets, load dataset, ...)
     *
     * @return initialized service for dataset management
     */
    @Bean
    public DatasetService getDatasetService() {
        return datasetService;
    }

    /**
     * Get initialized service for report management (to execute and export report, ...)
     *
     * @return initialized service for report management
     */
    @Bean
    public ReportService getReportService() {
        return reportService;
    }

    /**
     * Get initialized service for dataload processes management and process executions.
     *
     * @return initialized service for dataload processes management and process executions
     */
    @Bean
    public ProcessService getProcessService() {
        return processService;
    }

    /**
     * Get initialized service for ADS management (create, access and delete ads instances).
     *
     * @return initialized service for ADS management
     */
    @Bean
    public WarehouseService getWarehouseService() {
        return warehouseService;
    }

    /**
     * Get initialized service for connector integration management (create, update, start process, ...).
     *
     * @return initialized service for connector integration management
     */
    @Bean
    public ConnectorService getConnectorService() {
        return connectorService;
    }

    /**
     * Get initialized service for project notifications management.
     *
     * @return initialized service for project notifications management
     */
    @Bean
    public NotificationService getNotificationService() {
        return notificationService;
    }

    /**
     * Get initialized service for metadata export/import.
     *
     * @return initialized service for metadata export/import
     */
    @Bean
    public ExportImportService getExportImportService() {
        return exportImportService;
    }

    /**
     * Get initialized service for feature flag management.
     *
     * @return initialized service for feature flag management
     */
    @Bean
    public FeatureFlagService getFeatureFlagService() {
        return featureFlagService;
    }

    /**
     * Get initialized service for output stage management.
     *
     * @return initialized service for output stage management
     */
    @Bean
    public OutputStageService getOutputStageService() {
        return outputStageService;
    }
}
