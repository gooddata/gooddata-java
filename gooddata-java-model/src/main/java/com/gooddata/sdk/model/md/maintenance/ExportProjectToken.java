/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.maintenance;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import static com.gooddata.sdk.common.util.Validate.notEmpty;

/**
 * Complete project export token. Serves as configuration structure for import.
 * Serialization only.
 */
@JsonTypeName("importProject")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class ExportProjectToken {

    public static final String URI = "/gdc/md/{projectId}/maintenance/import";

    private final String token;

    /**
     * Creates new ExportProjectToken.
     *
     * @param token token identifying export of another project
     */
    public ExportProjectToken(String token) {
        this.token = notEmpty(token, "token");
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
