/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload.processes;

import com.gooddata.sdk.common.GoodDataException;

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
