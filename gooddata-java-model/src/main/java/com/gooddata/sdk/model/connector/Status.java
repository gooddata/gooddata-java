/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

import static com.gooddata.sdk.model.connector.Status.Code.ERROR;
import static com.gooddata.sdk.model.connector.Status.Code.SYNCHRONIZED;
import static com.gooddata.sdk.model.connector.Status.Code.USER_ERROR;

/**
 * Connector process status. Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
    private final String code;
    private final String detail;
    private final String description;

    @JsonCreator
    Status(@JsonProperty("code") String code, @JsonProperty("detail") String detail,
           @JsonProperty("description") String description) {
        this.code = code;
        this.detail = detail;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDetail() {
        return detail;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns true when the status means that the connector process has already finished
     * (no matter if it was successful).
     * NOTE: It also returns false in case of inability to resolve the code (e.g. API change)
     *
     * @return true when the status means that the connector process has already finished, false otherwise
     */
    @JsonIgnore
    public boolean isFinished() {
        return SYNCHRONIZED.name().equalsIgnoreCase(code) || isFailed();
    }

    /**
     * Returns true when the status means that the connector process failed.
     * NOTE: It also returns false in case of inability to resolve the code (e.g. API change)
     *
     * @return true when the status means that the connector process failed, false otherwise
     */
    @JsonIgnore
    public boolean isFailed() {
        return ERROR.name().equalsIgnoreCase(code) || USER_ERROR.name().equalsIgnoreCase(code);
    }


    /**
     * Enum of connector process status codes
     */
    public enum Code {
        NEW, SCHEDULED, DOWNLOADING, DOWNLOADED, TRANSFORMING, TRANSFORMED, UPLOADING, UPLOADED, SYNCHRONIZED,
        ERROR, USER_ERROR
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
