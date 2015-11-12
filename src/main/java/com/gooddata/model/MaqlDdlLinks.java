/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import com.gooddata.gdc.LinkEntries;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
