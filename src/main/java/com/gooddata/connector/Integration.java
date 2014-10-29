/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connector;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import static com.gooddata.util.Validate.notEmpty;

/**
 * Connector integration (i.e. one instance of configured ETL for loading of one GDC project).
 */
@JsonTypeName("integration")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Integration {

    public static final String URL = "/gdc/projects/{project}/connectors/{connector}/integration";

    private String projectTemplate;
    private boolean active;
    private final IntegrationProcessStatus lastFinishedProcess;
    private final IntegrationProcessStatus lastSuccessfulProcess;
    private final IntegrationProcessStatus runningProcess;

    public Integration(final String projectTemplate) {
        this(projectTemplate, true, null, null, null);
    }

    @JsonCreator
    Integration(@JsonProperty("projectTemplate") String projectTemplate, @JsonProperty("active") boolean active,
                @JsonProperty("lastFinishedProcess") IntegrationProcessStatus lastFinishedProcess,
                @JsonProperty("lastSuccessfulProcess") IntegrationProcessStatus lastSuccessfulProcess,
                @JsonProperty("runningProcess") IntegrationProcessStatus runningProcess) {
        this.projectTemplate = notEmpty(projectTemplate, "projectTemplate");
        this.active = active;
        this.lastFinishedProcess = lastFinishedProcess;
        this.lastSuccessfulProcess = lastSuccessfulProcess;
        this.runningProcess = runningProcess;
    }

    public String getProjectTemplate() {
        return projectTemplate;
    }

    public void setProjectTemplate(final String projectTemplate) {
        this.projectTemplate = notEmpty(projectTemplate, "projectTemplate");
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public IntegrationProcessStatus getLastFinishedProcess() {
        return lastFinishedProcess;
    }

    public IntegrationProcessStatus getLastSuccessfulProcess() {
        return lastSuccessfulProcess;
    }

    public IntegrationProcessStatus getRunningProcess() {
        return runningProcess;
    }
}
