/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import com.gooddata.sdk.common.util.ISOZonedDateTimeSerializer;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.TreeMap;

import static com.gooddata.sdk.model.connector.ConnectorType.ZENDESK4;
import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Zendesk 4 (Insights) connector process execution (i.e. definition for single ETL run). Serialization only.
 */
public class Zendesk4ProcessExecution implements ProcessExecution {

    private Boolean incremental;

    private Boolean reload;

    private Boolean recoverable;

    private Boolean recoveryInProgress;

    private Map<String, ZonedDateTime> startTimes;

    private DownloadParams downloadParams;

    @Override
    public ConnectorType getConnectorType() {
        return ZENDESK4;
    }

    public Boolean getIncremental() {
        return incremental;
    }

    public void setIncremental(final Boolean incremental) {
        this.incremental = incremental;
    }

    public Boolean getReload() {
        return reload;
    }

    /**
     * set by scheduler, when the process is actually a reload of a project
     */
    public void setReload(final Boolean reload) {
        this.reload = reload;
    }

    public Boolean getRecoverable() {
        return recoverable;
    }

    /**
     * Tells if the newly started process should use recoverable feature.
     * Usable from R176.
     */
    public void setRecoverable(final Boolean recoverable) {
        this.recoverable = recoverable;
    }

    public Boolean getRecoveryInProgress() {
        return recoveryInProgress;
    }

    /**
     * Tells if there is some recoverable process in progress for given project
     * Usable from R176.
     */
    public void setRecoveryInProgress(final Boolean recoveryInProgress) {
        this.recoveryInProgress = recoveryInProgress;
    }

    @JsonAnyGetter
    @JsonSerialize(contentUsing = ISOZonedDateTimeSerializer.class)
    public Map<String, ZonedDateTime> getStartTimes() {
        return startTimes;
    }


    public void setStartTime(final String resource, final ZonedDateTime startTime) {
        notEmpty(resource, "resource");
        notNull(startTime, "startTime");

        startTimes = startTimes == null ? new TreeMap<>() : startTimes;

        startTimes.put(resource + "StartDate", startTime);
    }

    @JsonIgnore
    public DownloadParams getDownloadParams() {
        if (downloadParams == null) {
            downloadParams = new DownloadParams();
        }
        return downloadParams;
    }

    @JsonProperty("downloadParams")
    private DownloadParams getDownloadParamsPlain() {
        return downloadParams;
    }

    public void setDownloadParams(DownloadParams downloadParams) {
        this.downloadParams = downloadParams;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DownloadParams {
        private Boolean useBackup;
        private Integer parallelWorkers;
        private Integer parallelBatchSeconds;

        public DownloadParams(Boolean useBackup, Integer parallelWorkers, Integer parallelBatchSeconds) {
            this.useBackup = useBackup;
            this.parallelWorkers = parallelWorkers;
            this.parallelBatchSeconds = parallelBatchSeconds;
        }

        public DownloadParams(Integer parallelWorkers, Integer parallelBatchSeconds) {
            this(null, parallelWorkers, parallelBatchSeconds);
        }

        public DownloadParams(Boolean useBackup) {
            this(useBackup, null, null);
        }

        private DownloadParams() {}

        @JsonProperty("useBackup")
        public Boolean getUseBackup() {
            return useBackup;
        }

        public void setUseBackup(Boolean useBackup) {
            this.useBackup = useBackup;
        }

        @JsonProperty("parallelWorkers")
        public Integer getParallelWorkers() {
            return parallelWorkers;
        }

        public void setParallelWorkers(Integer parallelWorkers) {
            this.parallelWorkers = parallelWorkers;
        }

        @JsonProperty("parallelBatchSeconds")
        public Integer getParallelBatchSeconds() {
            return parallelBatchSeconds;
        }

        public void setParallelBatchSeconds(Integer parallelBatchSeconds) {
            this.parallelBatchSeconds = parallelBatchSeconds;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
