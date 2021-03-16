/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.maintenance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.model.gdc.UriResponse;

/**
 * Complete project export result structure.
 * For internal use only.
 * Deserialization only.
 */
@JsonTypeName("exportArtifact")
public class ExportProjectArtifact extends PartialMdArtifact {

    @JsonCreator
    public ExportProjectArtifact(@JsonProperty("status") UriResponse status, @JsonProperty("token") String token) {
        super(status, token);
    }
}
