/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.report;

/**
 * Thrown when report export contains no data
 */
public class NoDataReportException extends ReportException {

    public NoDataReportException() {
        this("Report contains no data");
    }

    public NoDataReportException(final String message) {
        super(message);
    }

}
