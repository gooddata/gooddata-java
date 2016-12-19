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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.util.BooleanDeserializer;
import com.gooddata.util.GDDateTimeDeserializer;
import com.gooddata.util.GoodDataToStringBuilder;
import org.joda.time.DateTime;
import org.springframework.web.util.UriTemplate;

/**
 * Contains information about single dataset upload.
 * Deserialization only.
 */
@JsonTypeName("dataUpload")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Upload {

    public static final String URI = "/gdc/md/{projectId}/data/upload/{uploadId}";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(URI);

    private final String uri;
    private final String status;
    private final double progress;
    private final String message;
    private final UploadMode uploadMode;
    private final Integer size;
    private final DateTime createdAt;
    private final DateTime processedAt;

    Upload(@JsonProperty("msg") String message,
            @JsonProperty("progress") Double progress,
            @JsonProperty("status") String status,
            @JsonProperty("fullUpload") @JsonDeserialize(using = BooleanDeserializer.class) Boolean fullUpload,
            @JsonProperty("uri") String uri,
            @JsonProperty("createdAt") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime createdAt,
            @JsonProperty("fileSize") Integer size,
            @JsonProperty("processedAt") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime processedAt) {

        this.uri = uri;
        this.status = status;
        this.progress = progress != null ? progress : 0;
        this.message = message;
        this.uploadMode = toUploadMode(fullUpload);
        this.size = size;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    /**
     * @return uri link to self
     */
    public String getUri() {
        return uri;
    }

    /**
     * @return upload status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return upload progress in percent as floating point number
     */
    public double getProgress() {
        return progress;
    }

    /**
     * @return error message if the upload failed, {@code null} otherwise
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return {@link UploadMode} of this upload
     */
    public UploadMode getUploadMode() {
        return uploadMode;
    }

    /**
     * @return size of the data uploaded by this upload
     */
    public Integer getSize() {
        return size;
    }

    /**
     * @return date of creation of this upload
     */
    public DateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @return date when the upload was processed or {@code null} if upload is still being processed
     */
    public DateTime getProcessedAt() {
        return processedAt;
    }

    /**
     * Converts boolean value containing if the upload is full or incremental load to {@link UploadMode} enum.
     */
    private UploadMode toUploadMode(Boolean fullUpload) {
        if (fullUpload == null) {
            return null;
        }

        return fullUpload ? UploadMode.FULL : UploadMode.INCREMENTAL;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
