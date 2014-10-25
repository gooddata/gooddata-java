/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connector;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;

/**
 * Connector process (i.e. single ETL run) status (standalone, not embedded in integration as its parent) .
 * Deserialization only.
 */
@JsonTypeName("process")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessStatus extends IntegrationProcessStatus {

    public static final String URL = "/gdc/projects/{project}/connectors/{connector}/integration/processes";

    @JsonCreator
    ProcessStatus(@JsonProperty("status") Status status, @JsonProperty("started") String started,
                  @JsonProperty("finished") String finished) {
        super(status, started, finished);
    }

}
