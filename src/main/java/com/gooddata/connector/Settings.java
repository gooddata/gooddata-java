/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Connector integration settings.
 */
@JsonTypeName("settings")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Settings {

    /**
     * Settings URL dedicated for Zendesk4 connector only.
     * Other connectors have different URL than this one.
     *
     * @deprecated use {@link Zendesk4Settings#URL} instead
     */
    @Deprecated
    String URL = "/gdc/projects/{project}/connectors/{connector}/integration/settings";

    @JsonIgnore
    ConnectorType getConnectorType();

}
