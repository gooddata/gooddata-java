/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Collection;

/**
 * Contains collection of dataset uploads.
 * Deserialization only. For internal use only.
 */
@JsonTypeName("dataUploads")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
class Uploads {

    private Collection<Upload> uploads;

    Uploads(@JsonProperty("uploads") Collection<Upload> uploads) {
        this.uploads = uploads;
    }

    /**
     * @return all items of uploads collection
     */
    Collection<Upload> items() {
        return uploads;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.toStringExclude(this);
    }
}
