/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.auditevent;

import com.gooddata.collections.PageableListDeserializer;
import com.gooddata.collections.Paging;

import java.util.List;
import java.util.Map;

class AuditEventsDeserializer extends PageableListDeserializer<AuditEvents, AuditEvent>{

    protected AuditEventsDeserializer() {
        super(AuditEvent.class);
    }

    @Override
    protected AuditEvents createList(final List<AuditEvent> items, final Paging paging, final Map<String, String> links) {
        return new AuditEvents(items, paging, links);
    }
}
