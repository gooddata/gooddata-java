/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.auditevent;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

public class AuditEventTest {

    private static final String PARAM_KEY = "KEY";
    private static final String PARAM_VALUE = "VALUE";
    private static final String LINK_KEY = "LINK_KEY";
    private static final String LINK_VALUE = "LINK_VALUE";

    private static final DateTime DATE = new LocalDate(1993, 3, 9).toDateTimeAtStartOfDay(DateTimeZone.UTC);

    private final AuditEvent event = new AuditEvent("123", "bear@gooddata.com", DATE, DATE, "127.0.0.1", true, "login", emptyMap(), null);
    private final AuditEvent eventWithParams = new AuditEvent("123", "bear@gooddata.com", DATE, DATE, "127.0.0.1", true, "login",
            singletonMap(PARAM_KEY, PARAM_VALUE), null);

    private final AuditEvent eventWithParamsAndLinks = new AuditEvent("123", "bear@gooddata.com", DATE, DATE, "127.0.0.1", true, "login",
            singletonMap(PARAM_KEY, PARAM_VALUE),
            singletonMap(LINK_KEY, LINK_VALUE));

    @Test
    public void testSerialize() throws Exception {
        assertThat(event, jsonEquals(resource("auditevents/auditEvent.json")));
    }

    @Test
    public void testDeserialize() throws Exception {
        final AuditEvent deserializedObject = readObjectFromResource("/auditevents/auditEvent.json", AuditEvent.class);
        assertThat(deserializedObject, notNullValue());
        assertThat(deserializedObject.getId(), is(event.getId()));
        assertThat(deserializedObject.getOccurred(), is(event.getOccurred()));
        assertThat(deserializedObject.getRecorded(), is(event.getRecorded()));
        assertThat(deserializedObject.getUserLogin(), is(event.getUserLogin()));
        assertThat(deserializedObject.getUserIp(), is(event.getUserIp()));
        assertThat(deserializedObject.isSuccess(), is(event.isSuccess()));
        assertThat(deserializedObject.getType(), is(event.getType()));
    }

    @Test
    public void testSerializeEventWithParams() throws Exception {
        assertThat(eventWithParams, jsonEquals(resource("auditevents/auditEventWithParam.json")));
    }

    @Test
    public void testDeserializeWithParams() throws Exception {
        final AuditEvent deserializedObject = readObjectFromResource("/auditevents/auditEventWithParam.json", AuditEvent.class);
        assertThat(deserializedObject.getParams(), hasEntry("KEY", "VALUE"));
    }

    @Test
    public void testSerializeEventWithParamsAndLinks() throws Exception {
        assertThat(eventWithParamsAndLinks, jsonEquals(resource("auditevents/auditEventWithParamAndLink.json")));
    }

    @Test
    public void testDeserializeWithParamsAndLinks() throws Exception {
        final AuditEvent deserializedObject = readObjectFromResource("/auditevents/auditEventWithParamAndLink.json", AuditEvent.class);
        assertThat(deserializedObject.getParams(), hasEntry("KEY", "VALUE"));
        assertThat(deserializedObject.getLinks(), hasEntry("LINK_KEY", "LINK_VALUE"));
    }

}