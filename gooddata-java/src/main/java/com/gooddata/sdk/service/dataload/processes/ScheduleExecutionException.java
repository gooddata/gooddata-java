/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload.processes;

import com.gooddata.sdk.common.GoodDataException;

/**
 * Represents error during schedule execution
 */
public class ScheduleExecutionException extends GoodDataException {

    public ScheduleExecutionException(String message) {
        super(message);
    }

    public ScheduleExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
