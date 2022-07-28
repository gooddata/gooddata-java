/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project.model;

import com.gooddata.sdk.model.gdc.AbstractMaql;

/**
 * MAQL DDL statement.
 * Serialization only.
 */
public class MaqlDdl extends AbstractMaql {

    public static final String URI = "/gdc/md/{project}/ldm/manage2";

    public MaqlDdl(final String maql) {
        super(maql);
    }

}
