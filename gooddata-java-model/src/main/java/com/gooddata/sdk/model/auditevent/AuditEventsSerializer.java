/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.auditevent;

import com.gooddata.sdk.common.collections.PageSerializer;

class AuditEventsSerializer extends PageSerializer {

    public AuditEventsSerializer() {
        super(AuditEvents.ROOT_NODE);
    }
}

