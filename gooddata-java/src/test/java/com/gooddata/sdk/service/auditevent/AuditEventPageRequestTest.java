/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.auditevent;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.web.util.UriComponentsBuilder;
import org.testng.annotations.Test;

import static com.gooddata.collections.PageRequest.DEFAULT_LIMIT;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AuditEventPageRequestTest {

    private static final DateTime FROM = new DateTime();
    private static final DateTime TO = new DateTime();
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
        assertThat(result.getSanitizedLimit(), is(LIMIT+1));
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

        UriComponentsBuilder result = request.updateWithPageParams(UriComponentsBuilder.newInstance());

        assertThat(result.build().toUriString(), is(String.format("?offset=%s&limit=%d&from=%s&to=%s&type=%s",
                OFFSET, LIMIT, FROM.toDateTime(DateTimeZone.UTC), TO.toDateTime(DateTimeZone.UTC), EVENT_TYPE)));
    }

    @Test
    public void testUpdateWithOnlyPagingFields() {
        AuditEventPageRequest request = new AuditEventPageRequest();
        request.setLimit(LIMIT);
        request.setOffset(OFFSET);

        UriComponentsBuilder result = request.updateWithPageParams(UriComponentsBuilder.newInstance());

        assertThat(result.build().toUriString(), is("?offset=" + OFFSET + "&limit=" + LIMIT));
    }

    @Test
    public void testUpdateWithOnlyTimeIntervalFields() {
        AuditEventPageRequest request = new AuditEventPageRequest();
        request.setFrom(FROM);
        request.setTo(TO);

        UriComponentsBuilder result = request.updateWithPageParams(UriComponentsBuilder.newInstance());

        assertThat(result.build().toUriString(), is("?limit=" + DEFAULT_LIMIT +
                "&from=" + FROM.toDateTime(DateTimeZone.UTC) + "&to=" + TO.toDateTime(DateTimeZone.UTC)));
    }

    @Test
    public void shouldVerifyEquals() {
        EqualsVerifier.forClass(AuditEventPageRequest.class)
                .withRedefinedSuperclass()
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}
