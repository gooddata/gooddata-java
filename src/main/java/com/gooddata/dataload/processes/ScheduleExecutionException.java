/**
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload.processes;

import com.gooddata.GoodDataException;

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
