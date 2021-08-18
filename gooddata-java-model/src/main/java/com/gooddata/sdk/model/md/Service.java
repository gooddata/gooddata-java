/*
 * Copyright (C) 2004-2021, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;

/**
 * Represents project/workspace metadata configuration.
 */
@JsonTypeName("service")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Service implements Serializable {

    public static final String TIMEZONE_URI = "/gdc/md/{projectId}/service/timezone";

    private static final long serialVersionUID = -3382672258337809805L;

    private final String timezone;

    @JsonCreator
    private Service(@JsonProperty("timezone") String timezone) {
        this.timezone = timezone;
    }

    /**
     * @return string identifier of the timezone (see IANA/Olson tz database for possible values)
     */
    public String getTimezone() {
        return timezone;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
