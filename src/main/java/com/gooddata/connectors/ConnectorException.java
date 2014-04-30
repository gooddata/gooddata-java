package com.gooddata.connectors;

import com.gooddata.GoodDataException;

/**
 * Connector exception
 */
public class ConnectorException extends GoodDataException {

    public ConnectorException(final String message) {
        super(message);
    }

    public ConnectorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
