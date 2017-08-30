/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.report;

import com.gooddata.GoodDataException;

/**
 * Exception during report export
 * @deprecated use {@link com.gooddata.export.ExportException}
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
