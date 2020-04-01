/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload.processes;

import com.gooddata.GoodDataException;

/**
 * Process of the given URI doesn't exist
 */
public class ProcessNotFoundException extends GoodDataException {

    private final String uri;

    public ProcessNotFoundException(String uri, Throwable cause) {
        super("Process " + uri + " was not found", cause);
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
