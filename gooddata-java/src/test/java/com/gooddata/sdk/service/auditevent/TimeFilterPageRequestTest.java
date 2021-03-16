/*
 * (C) 2021 GoodData Corporation.
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

public class TimeFilterPageRequestTest {

    private static final ZonedDateTime FROM = ZonedDateTime.of(2019, 10, 28, 15, 30, 12, 0, UTC);
    private static final ZonedDateTime TO = ZonedDateTime.of(2019, 11, 25, 8, 5, 58, 125000000, UTC);
    private static final Integer LIMIT = 10;
    private static final String OFFSET = "foo";

    @Test
    public void testCopy() {
        TimeFilterPageRequest request = new TimeFilterPageRequest();
        request.setFrom(FROM);
        request.setTo(TO);
        request.setLimit(LIMIT);
        request.setOffset(OFFSET);

        TimeFilterPageRequest copy = TimeFilterPageRequest.copy(request);

        assertThat(request, is(sameBeanAs(copy)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCopyNull() {
        TimeFilterPageRequest.copy(null);
    }

    @Test
    public void testWithIncrementedLimit() {
        TimeFilterPageRequest request = new TimeFilterPageRequest();
        request.setFrom(FROM);
        request.setTo(TO);
        request.setLimit(LIMIT);
        request.setOffset(OFFSET);
        TimeFilterPageRequest result = request.withIncrementedLimit();

        assertThat(result.getFrom(), is(FROM));
        assertThat(result.getTo(), is(TO));
        assertThat(result.getSanitizedLimit(), is(LIMIT + 1));
        assertThat(result.getOffset(), is(OFFSET));
    }

    @Test
    public void testUpdateWithAllFields() {
        TimeFilterPageRequest request = new TimeFilterPageRequest();
        request.setFrom(FROM);
        request.setTo(TO);
        request.setLimit(LIMIT);
        request.setOffset(OFFSET);

        MutableUri result = request.updateWithPageParams(new SpringMutableUri(""));

        assertThat(result.toUriString(), is(String.format("?offset=%s&limit=%d&from=%s&to=%s",
                OFFSET, LIMIT, ISOZonedDateTime.FORMATTER.format(FROM.withZoneSameInstant(UTC)),
                ISOZonedDateTime.FORMATTER.format(TO.withZoneSameInstant(UTC)))));
    }

    @Test
    public void testUpdateWithOnlyPagingFields() {
        TimeFilterPageRequest request = new TimeFilterPageRequest();
        request.setLimit(LIMIT);
        request.setOffset(OFFSET);

        MutableUri result = request.updateWithPageParams(new SpringMutableUri(""));

        assertThat(result.toUriString(), is("?offset=" + OFFSET + "&limit=" + LIMIT));
    }

    @Test
    public void testUpdateWithOnlyTimeIntervalFields() {
        TimeFilterPageRequest request = new TimeFilterPageRequest();
        request.setFrom(FROM);
        request.setTo(TO);

        MutableUri result = request.updateWithPageParams(new SpringMutableUri(""));

        assertThat(result.toUriString(), is("?limit=" + DEFAULT_LIMIT +
                "&from=2019-10-28T15:30:12.000Z&to=2019-11-25T08:05:58.125Z"));
    }

    @Test
    public void shouldVerifyEquals() {
        EqualsVerifier.simple().forClass(TimeFilterPageRequest.class)
                .withRedefinedSuperclass()
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}
