/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.auditevent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;
import java.util.Map;

@JsonTypeInfo(
        include = JsonTypeInfo.As.WRAPPER_OBJECT,
        use = JsonTypeInfo.Id.NAME
)
@JsonTypeName("accessLogs")
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonSerialize(
        using = AccessLogsSerializer.class
)
@JsonDeserialize(
        using = AccessLogsDeserializer.class
)
public class AccessLogs extends Page<AccessLog> {
    static final String ROOT_NODE = "accessLogs";

    public AccessLogs(List<AccessLog> items, Paging paging, Map<String, String> links) {
        super(items, paging, links);
    }
}
