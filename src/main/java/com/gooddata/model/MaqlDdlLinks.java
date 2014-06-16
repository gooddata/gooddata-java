/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import com.gooddata.gdc.LinkEntries;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * MAQL DDL links (result from POSTing to /ldm/manage2).
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
class MaqlDdlLinks extends LinkEntries {

    private static final String TASKS_STATUS = "tasks-status";

    @JsonCreator
    private MaqlDdlLinks(@JsonProperty("entries") List<LinkEntry> entries) {
        super(entries);
    }

    public String getStatusLink() {
        for (LinkEntry linkEntry : getEntries()) {
            if (TASKS_STATUS.equals(linkEntry.getCategory())) {
                return linkEntry.getLink();
            }
        }
        return null;
    }
}
