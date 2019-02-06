/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.util.GDDateSerializer;
import com.gooddata.util.GoodDataToStringBuilder;
import org.joda.time.LocalDate;

/**
 * Coupa connector process execution (i.e. definition for single ETL run). Serialization only.
 */
public class CoupaProcessExecution implements ProcessExecution {

    private Boolean incremental;
    private LocalDate downloadDataFrom;

    @Override
    public ConnectorType getConnectorType() {
        return ConnectorType.COUPA;
    }

    public Boolean getIncremental() {
        return incremental;
    }

    public void setIncremental(Boolean incremental) {
        this.incremental = incremental;
    }

    @JsonSerialize(using = GDDateSerializer.class)
    public LocalDate getDownloadDataFrom() {
        return downloadDataFrom;
    }

    public void setDownloadDataFrom(LocalDate downloadDataFrom) {
        this.downloadDataFrom = downloadDataFrom;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
