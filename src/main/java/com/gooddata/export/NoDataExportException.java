/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.export;

import com.gooddata.report.NoDataReportException;

/**
 * Thrown when report export contains no data
 */
@SuppressWarnings("deprecation")
public class NoDataExportException extends NoDataReportException {

    public NoDataExportException() {
        this("Export contains no data");
    }

    public NoDataExportException(final String message) {
        super(message);
    }

}
