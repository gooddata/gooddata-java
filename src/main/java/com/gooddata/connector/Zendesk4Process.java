/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connector;

import static com.gooddata.connector.ConnectorType.ZENDESK4;

/**
 * Zendesk 4 (Insights) connector process execution. Serialization only.
 */
public class Zendesk4Process implements ProcessExecution {

    @Override
    public ConnectorType getConnectorType() {
        return ZENDESK4;
    }
}
