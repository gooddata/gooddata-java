/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.gooddata.GoodDataException;

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
