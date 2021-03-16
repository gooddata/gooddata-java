/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class RestrictionTest {

    @Test
    public void testIdentifier() {
        final Restriction id = Restriction.identifier("my id");
        assertThat(id, is(notNullValue()));
        assertThat(id.getType(), is(Restriction.Type.IDENTIFIER));
        assertThat(id.getValue(), is("my id"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testIdentifierWithNull() {
        Restriction.identifier(null);
    }

    @Test
    public void testTitle() {
        final Restriction id = Restriction.title("my title");
        assertThat(id, is(notNullValue()));
        assertThat(id.getType(), is(Restriction.Type.TITLE));
        assertThat(id.getValue(), is("my title"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTitleWithNull() {
        Restriction.title(null);
    }

    @Test
    public void testSummary() {
        final Restriction id = Restriction.summary("my summary");
        assertThat(id, is(notNullValue()));
        assertThat(id.getType(), is(Restriction.Type.SUMMARY));
        assertThat(id.getValue(), is("my summary"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSummaryWithNull() {
        Restriction.summary(null);
    }

    @Test
    public void testToStringFormat() {
        final Restriction id = Restriction.summary("my summary");
        assertThat(id.toString(), matchesPattern(Restriction.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(Restriction.class)
                .usingGetClass()
                .withNonnullFields("type", "value")
                .verify();
    }
}