/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.auditevent;

import com.gooddata.sdk.common.util.ISOZonedDateTime;
import com.gooddata.sdk.common.util.MutableUri;
import com.gooddata.sdk.common.util.SpringMutableUri;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.testng.annotations.Test;

import java.time.ZonedDateTime;

import static com.gooddata.sdk.common.collections.CustomPageRequest.DEFAULT_LIMIT;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AuditEventPageRequestTest {

    private static final ZonedDateTime FROM = ZonedDateTime.of(2019, 10, 28, 15, 30, 12, 0, UTC);
    private static final ZonedDateTime TO = ZonedDateTime.of(2019, 11, 25, 8, 5, 58, 125000000, UTC);
    private static final Integer LIMIT = 10;
    private static final String OFFSET = "foo";
    public static final String EVENT_TYPE = "STANDARD_LOGIN";

    @Test
    public void testCopy() {
        AuditEventPageRequest request = new AuditEventPageRequest();
        request.setFrom(FROM);
        request.setTo(TO);
        request.setLimit(LIMIT);
        request.setOffset(OFFSET);
        request.setType(EVENT_TYPE);

        AuditEventPageRequest copy = AuditEventPageRequest.copy(request);

        assertThat(request, is(sameBeanAs(copy)));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testCopyNull() {
        AuditEventPageRequest.copy(null);
    }

    @Test
    public void testWithIncrementedLimit() {
        AuditEventPageRequest request = new AuditEventPageRequest();
        request.setFrom(FROM);
        request.setTo(TO);
        request.setLimit(LIMIT);
        request.setOffset(OFFSET);
        request.setType(EVENT_TYPE);

        AuditEventPageRequest result = request.withIncrementedLimit();

        assertThat(result.getFrom(), is(FROM));
        assertThat(result.getTo(), is(TO));
        assertThat(result.getSanitizedLimit(), is(LIMIT + 1));
        assertThat(result.getOffset(), is(OFFSET));
        assertThat(result.getType(), is(EVENT_TYPE));
    }

    @Test
    public void testUpdateWithAllFields() {
        AuditEventPageRequest request = new AuditEventPageRequest();
        request.setFrom(FROM);
        request.setTo(TO);
        request.setLimit(LIMIT);
        request.setOffset(OFFSET);
        request.setType(EVENT_TYPE);

        MutableUri result = request.updateWithPageParams(new SpringMutableUri(""));

        assertThat(result.toUriString(), is(String.format("?offset=%s&limit=%d&from=%s&to=%s&type=%s",
                OFFSET, LIMIT, ISOZonedDateTime.FORMATTER.format(FROM.withZoneSameInstant(UTC)),
                ISOZonedDateTime.FORMATTER.format(TO.withZoneSameInstant(UTC)), EVENT_TYPE)));
    }

    @Test
    public void testUpdateWithOnlyPagingFields() {
        AuditEventPageRequest request = new AuditEventPageRequest();
        request.setLimit(LIMIT);
        request.setOffset(OFFSET);

        MutableUri result = request.updateWithPageParams(new SpringMutableUri(""));

        assertThat(result.toUriString(), is("?offset=" + OFFSET + "&limit=" + LIMIT));
    }

    @Test
    public void testUpdateWithOnlyTimeIntervalFields() {
        AuditEventPageRequest request = new AuditEventPageRequest();
        request.setFrom(FROM);
        request.setTo(TO);

        MutableUri result = request.updateWithPageParams(new SpringMutableUri(""));

        assertThat(result.toUriString(), is("?limit=" + DEFAULT_LIMIT +
                "&from=2019-10-28T15:30:12.000Z&to=2019-11-25T08:05:58.125Z"));
    }

    @Test
    public void shouldVerifyEquals() {
        EqualsVerifier.forClass(AuditEventPageRequest.class)
                .withRedefinedSuperclass()
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}
