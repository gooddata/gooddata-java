/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.connector;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.connector.ConnectorType;
import com.gooddata.sdk.model.project.Project;

import static java.lang.String.format;

/**
 * Integration for given project and connector type doesn't exist
 */
public class IntegrationNotFoundException extends GoodDataException {

    private final String projectUri;

    private final ConnectorType connectorType;

    public IntegrationNotFoundException(Project project, ConnectorType connectorType, Throwable e) {
        super(format("Integration %s was not found for project %s", connectorType.getName(), project.getUri()), e);
        this.projectUri = project.getUri();
        this.connectorType = connectorType;
    }

    public String getProjectUri() {
        return projectUri;
    }

    public ConnectorType getConnectorType() {
        return connectorType;
    }
}
