/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.util.GDDateSerializer;
import com.gooddata.util.GoodDataToStringBuilder;
import org.joda.time.LocalDate;

/**
 * Pardot connector process execution (i.e. definition for single ETL run). Serialization only.
 */
public class PardotProcessExecution implements ProcessExecution {

    private Boolean incremental;
    private LocalDate changesFrom;

    public Boolean getIncremental() {
        return incremental;
    }

    public void setIncremental(Boolean incremental) {
        this.incremental = incremental;
    }

    @JsonSerialize(using = GDDateSerializer.class)
    public LocalDate getChangesFrom() {
        return changesFrom;
    }

    public void setChangesFrom(LocalDate changesFrom) {
        this.changesFrom = changesFrom;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @Override
    public ConnectorType getConnectorType() {
        return ConnectorType.PARDOT;
    }
}
