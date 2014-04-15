/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;

/**
 * TODO
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("pullTask")
public class PullTask {

    public static final String URI = "/gdc/md/{projectId}/etl/task/{taskId}";

    private final String uri;

    @JsonCreator
    public PullTask(@JsonProperty("uri") String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
