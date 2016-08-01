/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.gdc.AboutLinks;

import java.util.List;

/**
 * Dataset links
 * @deprecated use {@link DatasetLinks} instead.
 */
@Deprecated
class Datasets extends AboutLinks {

    public static final String URI = "/gdc/md/{project}/ldm/singleloadinterface";

    @JsonCreator
    public Datasets(@JsonProperty("category") String category, @JsonProperty("summary") String summary,
                    @JsonProperty("links") List<Link> links) {
        super(category, summary, null, links);
    }

}
