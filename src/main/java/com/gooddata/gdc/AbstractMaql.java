/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

/**
 * Abstract MAQL statement.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("manage")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractMaql {

    @JsonProperty("maql")
    private final String maql;

    public AbstractMaql(String maql) {
        this.maql = maql;
    }

    public String getMaql() {
        return maql;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.toStringExclude(this);
    }
}
