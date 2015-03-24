/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.dataset;

import com.gooddata.gdc.AbstractMaql;

/**
 * MAQL DML statement.
 * Serialization only.
 */
class MaqlDml extends AbstractMaql {

    public static final String URI = "/gdc/md/{project}/dml/manage";

    public MaqlDml(final String maql) {
        super(maql);
    }
}
