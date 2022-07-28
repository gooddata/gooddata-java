/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md.maintenance;

import com.gooddata.sdk.common.GoodDataException;

/**
 * Exception thrown by ExportImportService when some task failed.
 */
public class ExportImportException extends GoodDataException {
    public ExportImportException(String message) {
        super(message);
    }

    public ExportImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
