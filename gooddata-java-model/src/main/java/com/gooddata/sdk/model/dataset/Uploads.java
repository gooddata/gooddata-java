/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collection;

/**
 * Contains collection of dataset uploads.
 * Deserialization only. For internal use only.
 */
@JsonTypeName("dataUploads")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Uploads {

    private final Collection<Upload> uploads;

    Uploads(@JsonProperty("uploads") Collection<Upload> uploads) {
        this.uploads = uploads;
    }

    /**
     * @return all items of uploads collection
     */
    public Collection<Upload> items() {
        return uploads;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
