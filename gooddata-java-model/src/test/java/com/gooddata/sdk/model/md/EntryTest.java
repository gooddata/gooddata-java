/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Set;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static java.time.ZoneOffset.UTC;
import static java.util.Collections.singleton;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class EntryTest {

    public static final String OBJ_ID = "ENTRY_ID";
    public static final String URI = "/gdc/md/PROJECT_ID/obj/" + OBJ_ID;
    public static final String AUTHOR = "/gdc/account/profile/AUTHOR_USER_ID";
    public static final String CONTRIBUTOR = "/gdc/account/profile/CONTRIBUTOR_USER_ID";
    public static final ZonedDateTime CREATED = LocalDateTime.of(2014, 4, 11, 13, 45, 54).atZone(UTC);
    public static final String SUMMARY = "Entry summary";
    public static final String TITLE = "Entry title";
    public static final ZonedDateTime UPDATED = LocalDateTime.of(2014, 4, 11, 13, 45, 55).atZone(UTC);
    public static final String CATEGORY = "ENTRY_CATEGORY";
    public static final Set<String> TAGS = singleton("TAG");
    public static final boolean DEPRECATED = true;
    public static final String IDENTIFIER = "ID";
    public static final boolean LOCKED = true;
    public static final boolean UNLISTED = false;

    @Test
    public void testDeserialize() {
        final Entry entry = readObjectFromResource("/md/entry.json", Entry.class);
        assertThat(entry, is(notNullValue()));
        assertThat(entry.getId(), is(OBJ_ID));
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

        assertThat(entry, jsonEquals(resource("md/entry.json")));
    }

    @Test
    public void testToStringFormat() {
        final Entry entry = new Entry(URI, TITLE, SUMMARY, CATEGORY, AUTHOR, CONTRIBUTOR, DEPRECATED, IDENTIFIER, TAGS,
                CREATED, UPDATED, LOCKED, UNLISTED);

        assertThat(entry.toString(), matchesPattern(Entry.class.getSimpleName() + "\\[.*\\]"));
    }

}

