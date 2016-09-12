/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataload.processes;

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
