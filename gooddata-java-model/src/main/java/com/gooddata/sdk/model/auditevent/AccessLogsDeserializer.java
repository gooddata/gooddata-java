/*
 * Copyright (C) 2004-2021, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.auditevent;

import com.gooddata.sdk.common.collections.PageDeserializer;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;
import java.util.Map;

public class AccessLogsDeserializer extends PageDeserializer<AccessLogs, AccessLog> {

    public AccessLogsDeserializer() { super(AccessLog.class); }

    @Override
    protected AccessLogs createPage(List<AccessLog> items, Paging paging, Map<String, String> links) {
        return new AccessLogs(items, paging, links);
    }
}
