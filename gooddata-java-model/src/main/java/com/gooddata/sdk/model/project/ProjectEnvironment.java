/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

/**
 * Optional property for project create, the property is ignored during project update.
 * Default value is {@link #PRODUCTION} which is also environment for all currently existing projects.<p>
 * Preffer {@link Environment} if possible.
 */
public enum ProjectEnvironment {
    /**
     * Default value, projects are backed-up and archived.
     */
    PRODUCTION,
    /**
     * no meaning yet and behavior is the same as for {@link #PRODUCTION} projects
     */
    DEVELOPMENT,
    /**
     * 'TESTING' projects are not backed-up and archived.
     */
    TESTING
}
