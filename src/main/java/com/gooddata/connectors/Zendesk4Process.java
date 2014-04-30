/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connectors;

/**
 * Zendesk4 connector process execution
 */
public class Zendesk4Process implements ProcessExecution {
    @Override
    public Connector getConnector() {
        return Connector.ZENDESK4;
    }
}
