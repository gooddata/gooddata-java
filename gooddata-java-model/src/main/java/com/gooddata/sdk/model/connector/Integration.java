/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import static com.gooddata.sdk.common.util.Validate.notEmpty;

/**
 * Connector integration (i.e. one instance of configured ETL for loading of one GDC project).
 */
@JsonTypeName("integration")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @JsonIgnore
    public IntegrationProcessStatus getLastFinishedProcess() {
        return lastFinishedProcess;
    }

    @JsonIgnore
    public IntegrationProcessStatus getLastSuccessfulProcess() {
        return lastSuccessfulProcess;
    }

    @JsonIgnore
    public IntegrationProcessStatus getRunningProcess() {
        return runningProcess;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
