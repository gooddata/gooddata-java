/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.report;

import com.gooddata.sdk.service.export.NoDataExportException;

/**
 * Thrown when export contains no data
 * @deprecated use {@link NoDataExportException}
 */
@SuppressWarnings("deprecation")
@Deprecated
public class NoDataReportException extends ReportException {

    public NoDataReportException() {
        this("Report contains no data");
    }

    public NoDataReportException(final String message) {
        super(message);
    }

}
