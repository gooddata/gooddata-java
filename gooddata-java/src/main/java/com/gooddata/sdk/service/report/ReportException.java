/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.report;

import com.gooddata.GoodDataException;
import com.gooddata.sdk.service.export.ExportException;

/**
 * Exception during report export
 * @deprecated use {@link ExportException}
 */
@Deprecated
public class ReportException extends GoodDataException {
    public ReportException(String message) {
        super(message);
    }

    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }
}
