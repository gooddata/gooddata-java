/*
 * (C) 2021 GoodData Corporation.
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
import static java.util.Collections.singletonMap;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class AccessLogsTest {

    private static final ZonedDateTime DATE = ZonedDateTime.of(LocalDate.of(1993, 3, 9).atStartOfDay(), UTC);

    private final AccessLog ACCESS_LOG_1 = new AccessLog("123", "visa.gooddata.com", "/gdc/ping", "GET", "200", "2231", "127.0.0.1", DATE, DATE);
    private final AccessLog ACCESS_LOG_2 = new AccessLog("456", "mastercard.gooddata.com", "/gdc/ping", "GET", "200", "2231", "127.0.0.1", DATE, DATE);

    private static final String DOMAIN = "default";
    private static final String RESOURCE_URI = new UriTemplate(AccessLog.RESOURCE_URI).expand(DOMAIN).toString();
    private static final String RESOURCE_NEXT_URI = RESOURCE_URI + "?offset=456";

    private final AccessLogs ACCESS_LOGS = new AccessLogs(
            asList(ACCESS_LOG_1, ACCESS_LOG_2),
            new Paging(RESOURCE_NEXT_URI),
            singletonMap("self", RESOURCE_URI)
    );

    private final AccessLogs EMPTY_ACCESS_LOGS = new AccessLogs(
            Collections.emptyList(),
            new Paging(null),
            singletonMap("self", RESOURCE_URI)
    );

    @Test
    public void shouldSerialize() throws Exception {
        assertThat(ACCESS_LOGS, jsonEquals(resource("auditevents/accessLogs.json")));
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final AccessLogs deserialized = readObjectFromResource("/auditevents/accessLogs.json", AccessLogs.class);
        assertThat(deserialized.getPaging().getNextUri(), is(RESOURCE_NEXT_URI));
        final List<AccessLog> pageItems = deserialized.getPageItems();
        assertThat(pageItems, hasSize(2));
        assertThat(pageItems.get(0).getId(), is(ACCESS_LOG_1.getId()));
        assertThat(pageItems.get(1).getId(), is(ACCESS_LOG_2.getId()));
    }

    @Test
    public void testSerializeEmptyAccessLogs() throws Exception {
        assertThat(EMPTY_ACCESS_LOGS, jsonEquals(resource("auditevents/emptyAccessLogs.json")));
    }

    @Test
    public void testDeserializeEmptyEvents() throws Exception {
        final AccessLogs deserialized = readObjectFromResource("/auditevents/emptyAccessLogs.json", AccessLogs.class);
        assertThat(deserialized.getPaging().getNextUri(), nullValue());
        assertThat(deserialized.getPageItems(), hasSize(0));
    }

}
