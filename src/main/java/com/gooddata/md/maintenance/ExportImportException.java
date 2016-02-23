package com.gooddata.md.maintenance;

import com.gooddata.GoodDataException;

/**
 * Exception thrown by MetadataService when export / import task failed for some reason.
 * Possible tasks are:
 * <ul>
 *     <li>Partial MD export</li>
 *     <li>Partial MD import</li>
 * </ul>
 */
public class ExportImportException extends GoodDataException {
    public ExportImportException(String message) {
        super(message);
    }

    public ExportImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
