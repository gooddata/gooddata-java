/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import com.gooddata.gdc.AbstractMaql;

/**
 * MAQL DDL statement.
 * Serialization only.
 */
class MaqlDdl extends AbstractMaql {

    public static final String URI = "/gdc/md/{project}/ldm/manage2";

    public MaqlDdl(final String maql) {
        super(maql);
    }

}
