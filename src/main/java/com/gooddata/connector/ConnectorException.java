package com.gooddata.connector;

import com.gooddata.GoodDataException;

/**
 * Represents error in ConnectorService.
 */
public class ConnectorException extends GoodDataException {

    public ConnectorException(final String message) {
        super(message);
    }

    public ConnectorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
