/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
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
