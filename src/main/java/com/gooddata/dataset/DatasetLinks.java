/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.gdc.AboutLinks;

import java.util.List;

/**
 * Dataset links.
 * Deserialization only.
 */
class DatasetLinks extends AboutLinks {

    public static final String URI = "/gdc/md/{project}/ldm/singleloadinterface";

    @JsonCreator
    public DatasetLinks(@JsonProperty("category") String category, @JsonProperty("summary") String summary,
                        @JsonProperty("links") List<Link> links) {
        super(category, summary, null, links);
    }

}
