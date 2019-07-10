/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import com.gooddata.sdk.service.account.AccountService;
import com.gooddata.sdk.service.auditevent.AuditEventService;
import com.gooddata.sdk.service.connector.ConnectorService;
import com.gooddata.sdk.service.dataload.OutputStageService;
import com.gooddata.sdk.service.dataload.processes.ProcessService;
import com.gooddata.sdk.service.dataset.DatasetService;
import com.gooddata.sdk.service.executeafm.ExecuteAfmService;
import com.gooddata.sdk.service.export.ExportService;
import com.gooddata.sdk.service.featureflag.FeatureFlagService;
import com.gooddata.sdk.service.gdc.DataStoreService;
import com.gooddata.sdk.service.gdc.GdcService;
import com.gooddata.sdk.service.httpcomponents.LoginPasswordGoodDataRestProvider;
import com.gooddata.sdk.service.lcm.LcmService;
import com.gooddata.sdk.service.md.MetadataService;
import com.gooddata.sdk.service.md.maintenance.ExportImportService;
import com.gooddata.sdk.service.notification.NotificationService;
import com.gooddata.sdk.service.project.ProjectService;
import com.gooddata.sdk.service.project.model.ModelService;
import com.gooddata.sdk.service.projecttemplate.ProjectTemplateService;
import com.gooddata.sdk.service.warehouse.WarehouseService;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import static com.gooddata.sdk.service.GoodDataEndpoint.*;
import static com.gooddata.util.Validate.notNull;

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

    private final GoodDataServices services;

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
        this(new LoginPasswordGoodDataRestProvider(
                new GoodDataEndpoint(hostname, port, protocol),
                settings,
                login,
                password
        ));
    }

    /**
     * Create instance based on given {@link GoodDataRestProvider}.
     * @param goodDataRestProvider configured provider
     */
    protected GoodData(final GoodDataRestProvider goodDataRestProvider) {
        this.services = new GoodDataServices(notNull(goodDataRestProvider, "goodDataRestProvider"));
    }

    /**
     * @return underlying RestTemplate
     */
    protected RestTemplate getRestTemplate() {
        return services.getRestTemplate();
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
        return services.getProjectService();
    }

    /**
     * Get initialized service for account management (to get current account information, logout, ...)
     *
     * @return initialized service for account management
     */
    @Bean("goodDataAccountService")
    public AccountService getAccountService() {
        return services.getAccountService();
    }

    /**
     * Get initialized service for metadata management (to query, create and update project metadata like attributes,
     * facts, metrics, reports, ...)
     *
     * @return initialized service for metadata management
     */
    @Bean("goodDataMetadataService")
    public MetadataService getMetadataService() {
        return services.getMetadataService();
    }

    /**
     * Get initialized service for model management (to get model diff, update model, ...)
     *
     * @return initialized service for model management
     */
    @Bean("goodDataModelService")
    public ModelService getModelService() {
        return services.getModelService();
    }

    /**
     * Get initialized service for API root management (to get API root links, ...)
     *
     * @return initialized service for API root management
     */
    @Bean("goodDataGdcService")
    public GdcService getGdcService() {
        return services.getGdcService();
    }

    /**
     * Get initialized service for data store (user staging/WebDAV) management (to upload, download, delete, ...)
     *
     * @return initialized service for data store management
     */
    @Bean("goodDataDataStoreService")
    public DataStoreService getDataStoreService() {
        return services.getDataStoreService();
    }

    /**
     * Get initialized service for dataset management (to list manifest, get datasets, load dataset, ...)
     *
     * @return initialized service for dataset management
     */
    @Bean("goodDataDatasetService")
    public DatasetService getDatasetService() {
        return services.getDatasetService();
    }

    /**
     * Get initialized service for exports management (export report,...)
     *
     * @return initialized service for exports
     */
    @Bean("goodDataExportService")
    public ExportService getExportService() {
        return services.getExportService();
    }

    /**
     * Get initialized service for dataload processes management and process executions.
     *
     * @return initialized service for dataload processes management and process executions
     */
    @Bean("goodDataProcessService")
    public ProcessService getProcessService() {
        return services.getProcessService();
    }

    /**
     * Get initialized service for ADS management (create, access and delete ads instances).
     *
     * @return initialized service for ADS management
     */
    @Bean("goodDataWarehouseService")
    public WarehouseService getWarehouseService() {
        return services.getWarehouseService();
    }

    /**
     * Get initialized service for connector integration management (create, update, start process, ...).
     *
     * @return initialized service for connector integration management
     */
    @Bean("goodDataConnectorService")
    public ConnectorService getConnectorService() {
        return services.getConnectorService();
    }

    /**
     * Get initialized service for project notifications management.
     *
     * @return initialized service for project notifications management
     */
    @Bean("goodDataNotificationService")
    public NotificationService getNotificationService() {
        return services.getNotificationService();
    }

    /**
     * Get initialized service for metadata export/import.
     *
     * @return initialized service for metadata export/import
     */
    @Bean("goodDataExportImportService")
    public ExportImportService getExportImportService() {
        return services.getExportImportService();
    }

    /**
     * Get initialized service for feature flag management.
     *
     * @return initialized service for feature flag management
     */
    @Bean("goodDataFeatureFlagService")
    public FeatureFlagService getFeatureFlagService() {
        return services.getFeatureFlagService();
    }

    /**
     * Get initialized service for output stage management.
     *
     * @return initialized service for output stage management
     */
    @Bean("goodDataOutputStageService")
    public OutputStageService getOutputStageService() {
        return services.getOutputStageService();
    }

    /**
     * Get initialized service for project templates
     *
     * @return initialized service for project templates
     */
    @Bean("goodDataProjectTemplateService")
    public ProjectTemplateService getProjectTemplateService() {
        return services.getProjectTemplateService();
    }

    /**
     * Get initialized service for audit events
     * @return initialized service for audit events
     */
    @Bean("goodDataAuditEventService")
    public AuditEventService getAuditEventService() {
        return services.getAuditEventService();
    }

    /**
     * Get initialized service for afm execution
     * @return initialized service for afm execution
     */
    @Bean("goodDataExecuteAfmService")
    public ExecuteAfmService getExecuteAfmService() {
        return services.getExecuteAfmService();
    }

    /**
     * Get initialized service for Life Cycle Management
     * @return initialized service for Life Cycle Management
     */
    @Bean("goodDataLcmService")
    public LcmService getLcmService() {
        return services.getLcmService();
    }
}
