/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.project;

import com.gooddata.GoodDataException;

/**
 * Project role of the given URI doesn't exist
 */
public class RoleNotFoundException extends GoodDataException {

    private final String uri;

    public RoleNotFoundException(String uri, Throwable cause) {
        super("Role " + uri + " was not found", cause);
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
