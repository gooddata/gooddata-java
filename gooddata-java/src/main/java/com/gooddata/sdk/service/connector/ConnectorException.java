/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.connector;

import com.gooddata.sdk.common.GoodDataException;

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
