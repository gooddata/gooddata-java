package com.gooddata.dataset.uploads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.dataset.UploadMode;
import com.gooddata.dataset.UploadStatus;
import com.gooddata.util.BooleanIntegerDeserializer;
import com.gooddata.util.GDDateTimeDeserializer;
import org.joda.time.DateTime;
import org.springframework.web.util.UriTemplate;

//TODO javadoc
//deserialization only
@JsonTypeName("dataUpload")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Upload extends AbstractUpload {

    public static final String URI = "/gdc/md/{projectId}/data/upload/{uploadId}";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(URI);

    private final String etlInterfaceUri;
    private final UploadMode uploadMode;
    private final Integer size;
    private final DateTime createdAt;
    private final DateTime processedAt;

    Upload(@JsonProperty("msg") String message,
            @JsonProperty("etlInterface") String etlInterfaceUri,
            @JsonProperty("progress") Double progress,
            @JsonProperty("status") UploadStatus status,
            @JsonProperty("fullUpload") @JsonDeserialize(using = BooleanIntegerDeserializer.class) Boolean fullUpload,
            @JsonProperty("uri") String uri,
            @JsonProperty("createdAt") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime createdAt,
            @JsonProperty("fileSize") Integer size,
            @JsonProperty("processedAt") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime processedAt) {
        super(uri, status, progress, message);

        this.etlInterfaceUri = etlInterfaceUri;
        this.uploadMode = toUploadMode(fullUpload);
        this.size = size;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public String getEtlInterfaceUri() {
        return etlInterfaceUri;
    }

    public UploadMode getUploadMode() {
        return uploadMode;
    }

    public Integer getSize() {
        return size;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getProcessedAt() {
        return processedAt;
    }

    private UploadMode toUploadMode(Boolean fullUpload) {
        if (fullUpload == null) {
            return null;
        }

        return fullUpload ? UploadMode.FULL : UploadMode.INCREMENTAL;
    }
}
