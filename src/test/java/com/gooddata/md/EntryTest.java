/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.util.Set;

import static com.gooddata.JsonMatchers.serializesToJson;
import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class EntryTest {

    public static final String URI = "/gdc/md/PROJECT_ID/obj/ENTRY_ID";
    public static final String AUTHOR = "/gdc/account/profile/AUTHOR_USER_ID";
    public static final String CONTRIBUTOR = "/gdc/account/profile/CONTRIBUTOR_USER_ID";
    public static final DateTime CREATED = new DateTime(2014, 4, 11, 13, 45, 54, DateTimeZone.UTC);
    public static final String SUMMARY = "Entry summary";
    public static final String TITLE = "Entry title";
    public static final DateTime UPDATED = new DateTime(2014, 4, 11, 13, 45, 55, DateTimeZone.UTC);
    public static final String CATEGORY = "ENTRY_CATEGORY";
    public static final Set<String> TAGS = singleton("TAG");
    public static final boolean DEPRECATED = true;
    public static final String IDENTIFIER = "ID";
    public static final boolean LOCKED = true;
    public static final boolean UNLISTED = false;

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserialize() throws Exception {
        final Entry entry = new ObjectMapper().readValue(getClass().getResourceAsStream("/md/entry.json"), Entry.class);
        assertThat(entry, is(notNullValue()));
        assertThat(entry.getLink(), is(URI));
        assertThat(entry.getUri(), is(URI));
        assertThat(entry.getTitle(), is(TITLE));
        assertThat(entry.getSummary(), is(SUMMARY));
        assertThat(entry.getCategory(), is(CATEGORY));
        assertThat(entry.getAuthor(), is(AUTHOR));
        assertThat(entry.getContributor(), is(CONTRIBUTOR));
        assertThat(entry.isDeprecated(), is(DEPRECATED));
        assertThat(entry.getIdentifier(), is(IDENTIFIER));
        assertThat(entry.getTags(), is(TAGS));
        assertThat(entry.getCreated(), is(CREATED));
        assertThat(entry.getUpdated(), is(UPDATED));
        assertThat(entry.isLocked(), is(LOCKED));
        assertThat(entry.isUnlisted(), is(UNLISTED));
    }

    @Test
    public void testSerialization() throws Exception {
        final Entry entry = new Entry(URI, TITLE, SUMMARY, CATEGORY, AUTHOR, CONTRIBUTOR, DEPRECATED, IDENTIFIER, TAGS,
                CREATED, UPDATED, LOCKED, UNLISTED);

        assertThat(entry, serializesToJson("/md/entry.json"));
    }

    @Test
    public void testToStringFormat() {
        final Entry entry = new Entry(URI, TITLE, SUMMARY, CATEGORY, AUTHOR, CONTRIBUTOR, DEPRECATED, IDENTIFIER, TAGS,
                CREATED, UPDATED, LOCKED, UNLISTED);

        assertThat(entry.toString(), matchesPattern(Entry.class.getSimpleName() + "\\[.*\\]"));
    }

}