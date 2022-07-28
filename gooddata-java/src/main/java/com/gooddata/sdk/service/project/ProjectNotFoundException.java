/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.project;

import com.gooddata.sdk.common.GoodDataException;

/**
 * Project of the given URI doesn't exist
 */
public class ProjectNotFoundException extends GoodDataException {

    private final String projectUri;

    public ProjectNotFoundException(String projectUri, Throwable cause) {
        super("Project " + projectUri + " was not found", cause);
        this.projectUri = projectUri;
    }

    public String getProjectUri() {
        return projectUri;
    }
}
