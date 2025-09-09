/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collection;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Encapsulates list of {@link DatasetManifest}.
 */
public class DatasetManifests {

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

