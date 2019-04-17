/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.model.md.Meta;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.gooddata.util.Validate.notEmpty;

/**
 * Contains information about dataset uploads for every single dataset in the project.
 * For more about dataset uploads information, see {@link DataSet}.
 * Deserialization only. Internal use only
 */
@JsonTypeName("dataSetsInfo")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadsInfo {

    public static final String URI = "/gdc/md/{projectId}/data/sets";

    private final Map<String, DataSet> datasets = new HashMap<>();

    //for deserialization and testing purposes only
    UploadsInfo(@JsonProperty("sets") Collection<DataSet> datasets) {
        if (datasets != null) {
            for (DataSet dataset : datasets) {
                this.datasets.put(dataset.getDatasetId(), dataset);
            }
        }
    }

    /**
     * Returns dataset uploads information for a dataset with the given ID.
     *
     * @param datasetId dataset identifier
     * @return {@link DataSet} object
     */
    public DataSet getDataSet(String datasetId) {
        notEmpty(datasetId, "datasetId");

        final DataSet dataSet = datasets.get(datasetId);
        if (dataSet != null) {
            return dataSet;
        } else {
            throw new DatasetNotFoundException(datasetId);
        }
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    /**
     * Contains information about dataset uploads for one dataset in the project. Information contain:
     * <ul>
     *      <li>dataset ID</li>
     *      <li>uri to all uploads for the given dataset</li>
     *      <li>last upload</li>
     * </ul>
     *
     * Deserialization only.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataSet {

        private final Meta meta;
        private final String datasetUploadsUri;
        private final LastUpload lastUpload;

        private DataSet(
                @JsonProperty("meta") Meta meta,
                @JsonProperty("dataUploads") String datasetUploadsUri,
                @JsonProperty("lastUpload") LastUpload lastUpload) {
            this.meta = meta;
            this.datasetUploadsUri = datasetUploadsUri;
            this.lastUpload = lastUpload;
        }

        /**
         * @return dataset identifier
         */
        public String getDatasetId() {
            return meta.getIdentifier();
        }

        /**
         * @return URI for all uploads of this dataset
         */
        public String getUploadsUri() {
            return datasetUploadsUri;
        }

        /**
         * @return {@link Upload} uri of the last upload
         */
        public String getLastUploadUri() {
            return lastUpload.uri;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }

    /**
     * Wrapper class for the last upload.
     * Only for deserialization. For internal use only.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonTypeName("dataUploadShort")
    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    private static class LastUpload {

        private final String uri;

        private LastUpload(@JsonProperty("uri") String uri) {
            this.uri = uri;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
