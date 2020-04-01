/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
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

/**
 * Pageable list DTO for Audit events
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(AuditEvents.ROOT_NODE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(using = AuditEventsSerializer.class)
@JsonDeserialize(using = AuditEventsDeserializer.class)
public class AuditEvents extends Page<AuditEvent> {

    static final String ROOT_NODE = "events";

    public AuditEvents(final List<AuditEvent> items, final Paging paging, final Map<String, String> links) {
        super(items, paging, links);
    }

}
