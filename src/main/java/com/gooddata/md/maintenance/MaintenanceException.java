package com.gooddata.md.maintenance;

import com.gooddata.GoodDataException;

/**
 * Exception thrown by MetadataService when some maintenance task failed for some reason.
 * Maintenance tasks are:
 * <ul>
 *     <li>Partial MD export</li>
 *     <li>Partial MD import</li>
 * </ul>
 */
public class MaintenanceException extends GoodDataException {
    public MaintenanceException(String message) {
        super(message);
    }

    public MaintenanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
