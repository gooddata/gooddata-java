/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.auditevent;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static java.time.ZoneOffset.UTC;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class AccessLogTest {

    private static final ZonedDateTime DATE = ZonedDateTime.of(LocalDate.of(1993, 3, 9).atStartOfDay(), UTC);

    private final AccessLog ACCESS_LOG = new AccessLog("123", "visa.gooddata.com", "/gdc/ping", "GET", "200", "2231", "127.0.0.1", DATE, DATE);

    @Test
    public void testSerialize() throws Exception {
        assertThat(ACCESS_LOG, jsonEquals(resource("auditevents/accessLog.json")));
    }

    @Test
    public void testDeserialize() throws Exception {
        final AccessLog deserializedObject = readObjectFromResource("/auditevents/accessLog.json", AccessLog.class);
        assertThat(deserializedObject, notNullValue());
        assertThat(deserializedObject.getId(), is(ACCESS_LOG.getId()));
        assertThat(deserializedObject.getHost(), is(ACCESS_LOG.getHost()));
        assertThat(deserializedObject.getPath(), is(ACCESS_LOG.getPath()));
        assertThat(deserializedObject.getMethod(), is(ACCESS_LOG.getMethod()));
        assertThat(deserializedObject.getCode(), is(ACCESS_LOG.getCode()));
        assertThat(deserializedObject.getSize(), is(ACCESS_LOG.getSize()));
        assertThat(deserializedObject.getUserIp(), is(ACCESS_LOG.getUserIp()));
        assertThat(deserializedObject.getOccurred(), is(ACCESS_LOG.getOccurred()));
        assertThat(deserializedObject.getRecorded(), is(ACCESS_LOG.getRecorded()));
    }

}
