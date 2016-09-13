/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload.processes;

import com.gooddata.GoodDataException;

/**
 * Schedule of the given URI doesn't exist
 */
public class ScheduleNotFoundException extends GoodDataException {

    private final String uri;

    public ScheduleNotFoundException(String uri, Throwable cause) {
        super("Schedule " + uri + " was not found", cause);
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
