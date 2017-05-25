/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Global statistics about project's uploads
 */
@JsonTypeName("dataUploadsInfo")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadStatistics {

    public static final String URI = "/gdc/md/{projectId}/data/uploads_info";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(URI);

    private final Map<String, Integer> statusesCount;

    private UploadStatistics(@JsonProperty("statusesCount") Map<String, Integer> statusesCount) {
        this.statusesCount = new HashMap<>(statusesCount);
    }

    /**
     * Returns count of uploads finished in given status.
     *
     * @param uploadStatus status of uploads to be counted
     * @return count of uploads or zero when statistics for given status don't exist
     */
    public int getUploadsCount(String uploadStatus) {
        final Integer uploadsCount = statusesCount.get(uploadStatus);

        return uploadsCount != null ? uploadsCount : 0;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
