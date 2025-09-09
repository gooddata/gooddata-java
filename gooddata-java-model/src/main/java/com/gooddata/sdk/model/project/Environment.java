/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

/**
 * Optional property for project or warehouse create, the property is ignored during update.
 * Default value is {@link #PRODUCTION} which is also environment for all currently existing projects or warehouses.
 */
public enum Environment {
    /** Default value, projects or warehouses are backed-up and archived. */
    PRODUCTION,
    /** no meaning yet and behavior is the same as for {@link #PRODUCTION} projects or warehouses. */
    DEVELOPMENT,
    /** 'TESTING' projects or warehouses are not backed-up and archived. */
    TESTING
}

