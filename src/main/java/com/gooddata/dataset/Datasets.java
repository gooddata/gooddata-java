/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Collection;
import java.util.List;

/**
 * Datasets
 */
@JsonTypeName("about")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
// todo this is almost copy of Gdc
public class Datasets {

    public static final String URI = "/gdc/md/{project}/ldm/singleloadinterface";

    private final String category;
    private final String summary;
    private final List<Dataset> links;

    @JsonCreator
    public Datasets(@JsonProperty("category") String category, @JsonProperty("summary") String summary,
                    @JsonProperty("links") List<Dataset> links) {
        this.category = category;
        this.summary = summary;
        this.links = links;
    }

    public String getCategory() {
        return category;
    }

    public String getSummary() {
        return summary;
    }

    public Collection<Dataset> getLinks() {
        return links;
    }

}
