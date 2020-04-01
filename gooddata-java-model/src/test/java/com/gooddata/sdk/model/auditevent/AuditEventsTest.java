/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.auditevent;

import com.gooddata.sdk.common.collections.Paging;
import org.springframework.web.util.UriTemplate;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class AuditEventsTest {

    private static final String USER1_ID = "user123";

    private static final String USER1_LOGIN = "bear@gooddata.com";
    private static final String USER2_LOGIN = "jane@gooddata.com";

    private static final String DOMAIN = "default";

    private static final String IP = "127.0.0.1";
    private static final boolean SUCCESS = true;
    private static final String TYPE = "login";

    private static final ZonedDateTime DATE = ZonedDateTime.of(LocalDate.of(1993, 3, 9).atStartOfDay(), UTC);
    private static final AuditEvent EVENT_1 = new AuditEvent("123", USER1_LOGIN, DATE, DATE, IP, SUCCESS, TYPE, emptyMap(), emptyMap());
    private static final AuditEvent EVENT_2 = new AuditEvent("456", USER2_LOGIN, DATE, DATE, IP, SUCCESS, TYPE, emptyMap(), emptyMap());

    private static final String ADMIN_URI = new UriTemplate(AuditEvent.ADMIN_URI).expand(DOMAIN).toString();
    private static final String USER_URI = new UriTemplate(AuditEvent.USER_URI).expand(USER1_ID).toString();
    private static final String ADMIN_NEXT_URI = ADMIN_URI + "?offset=456";
    private static final String USER_NEXT_URI = USER_URI + "?offset=456&limit=1";

    private static final AuditEvents EVENTS = new AuditEvents(
            asList(EVENT_1, EVENT_2),
            new Paging(ADMIN_NEXT_URI),
            singletonMap("self", ADMIN_URI)
    );

    private static final AuditEvents EMPTY_EVENTS = new AuditEvents(
            Collections.emptyList(),
            new Paging(null),
            singletonMap("self", ADMIN_URI)
    );

    private static final AuditEvents USER_EVENTS = new AuditEvents(
            singletonList(EVENT_1),
            new Paging(USER_NEXT_URI),
            singletonMap("self", USER_URI)
    );

    @Test
    public void testSerialize() throws Exception {
        assertThat(EVENTS, jsonEquals(resource("auditevents/auditEvents.json")));
    }

    @Test
    public void testDeserialize() throws Exception {
        final AuditEvents deserialized = readObjectFromResource("/auditevents/auditEvents.json", AuditEvents.class);
        assertThat(deserialized.getPaging().getNextUri(), is(ADMIN_NEXT_URI));
        final List<AuditEvent> pageItems = deserialized.getPageItems();
        assertThat(pageItems, hasSize(2));
        assertThat(pageItems.get(0).getId(), is(EVENT_1.getId()));
        assertThat(pageItems.get(1).getId(), is(EVENT_2.getId()));
    }

    @Test
    public void testSerializeEmptyEvents() throws Exception {
        assertThat(EMPTY_EVENTS, jsonEquals(resource("auditevents/emptyAuditEvents.json")));
    }

    @Test
    public void testDeserializeEmptyEvents() throws Exception {
        final AuditEvents deserialized = readObjectFromResource("/auditevents/emptyAuditEvents.json", AuditEvents.class);
        assertThat(deserialized.getPaging().getNextUri(), nullValue());
        assertThat(deserialized.getPageItems(), hasSize(0));
    }

    @Test
    public void testSerializeUserEvents() throws Exception {
        assertThat(USER_EVENTS, jsonEquals(resource("auditevents/userAuditEvents.json")));
    }

    @Test
    public void testDeserializeUserEvents() throws Exception {
        final AuditEvents deserialized = readObjectFromResource("/auditevents/userAuditEvents.json", AuditEvents.class);
        assertThat(deserialized.getPaging().getNextUri(), is(USER_NEXT_URI));
        assertThat(deserialized.getPageItems(), hasSize(1));
        assertThat(deserialized.getPageItems().get(0).getId(), is(EVENT_1.getId()));
    }
}