/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.auditevent;

import com.gooddata.sdk.common.collections.PageDeserializer;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;
import java.util.Map;

class AuditEventsDeserializer extends PageDeserializer<AuditEvents, AuditEvent>{

    protected AuditEventsDeserializer() {
        super(AuditEvent.class);
    }

    @Override
    protected AuditEvents createPage(final List<AuditEvent> items, final Paging paging, final Map<String, String> links) {
        return new AuditEvents(items, paging, links);
    }
}

