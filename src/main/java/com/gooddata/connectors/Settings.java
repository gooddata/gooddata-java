/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connectors;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Integration settings
 */
public interface Settings {

    final String URL = "/gdc/projects/{project}/connectors/{connector}/integration/settings";

    @JsonIgnore
    Connector getConnector();

}
