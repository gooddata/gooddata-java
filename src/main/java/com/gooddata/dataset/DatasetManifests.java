/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
