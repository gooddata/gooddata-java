/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.auditevent;

import com.gooddata.sdk.common.collections.PageSerializer;

public class AccessLogsSerializer extends PageSerializer {
    public AccessLogsSerializer() {
        super(AccessLogs.ROOT_NODE);
    }
}
