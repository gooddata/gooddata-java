/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.model.gdc.AboutLinks;

import java.util.List;

/**
 * Dataset links.
 * Deserialization only.
 */
public class DatasetLinks extends AboutLinks {

    public static final String URI = "/gdc/md/{project}/ldm/singleloadinterface";

    @JsonCreator
    public DatasetLinks(@JsonProperty("category") String category, @JsonProperty("summary") String summary,
                        @JsonProperty("links") List<Link> links) {
        super(category, summary, null, links);
    }

}
