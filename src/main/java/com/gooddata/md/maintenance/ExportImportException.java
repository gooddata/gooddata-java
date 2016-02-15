package com.gooddata.md.maintenance;

import com.gooddata.GoodDataException;

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
