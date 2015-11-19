/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

import static com.gooddata.util.Validate.notNull;

/**
 * Encapsulates list of {@link DatasetManifest}.
 */
class DatasetManifests {

    private final Collection<DatasetManifest> manifests;

    /**
     * Construct object.
     * @param manifests dataset upload manifests
     */
    @JsonCreator
    public DatasetManifests(@JsonProperty("dataSetSLIManifestList") Collection<DatasetManifest> manifests) {
        this.manifests = notNull(manifests, "manifests");
    }

    @JsonProperty("dataSetSLIManifestList")
    public Collection<DatasetManifest> getManifests() {
        return manifests;
    }
}
