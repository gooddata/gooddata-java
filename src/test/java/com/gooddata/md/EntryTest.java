/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class EntryTest {

    public static final String LINK = "/gdc/md/PROJECT_ID/obj/ENTRY_ID";
    public static final String AUTHOR = "/gdc/account/profile/AUTHOR_USER_ID";
    public static final String CONTRIBUTOR = "/gdc/account/profile/CONTRIBUTOR_USER_ID";
    public static final String CREATED = "2014-04-11 13:45:54";
    public static final String SUMMARY = "Entry summary";
    public static final String TITLE = "Entry title";
    public static final String UPDATED = "2014-04-11 13:45:55";
    public static final String CATEGORY = "ENTRY_CATEGORY";
    public static final String TAGS = "TAG";
    public static final String DEPRECATED = "1";
    public static final String IDENTIFIER = "ID";
    public static final Integer LOCKED = 1;
    public static final Integer UNLISTED = 0;

    @Test
    public void testDeserialize() throws Exception {
        final Entry entry = new ObjectMapper().readValue(getClass().getResourceAsStream("/md/entry.json"), Entry.class);
        assertThat(entry, is(notNullValue()));
        assertThat(entry.getLink(), is(LINK));
        assertThat(entry.getTitle(), is(TITLE));
        assertThat(entry.getSummary(), is(SUMMARY));
        assertThat(entry.getCategory(), is(CATEGORY));
        assertThat(entry.getAuthor(), is(AUTHOR));
        assertThat(entry.getContributor(), is(CONTRIBUTOR));
        assertThat(entry.getDeprecated(), is(DEPRECATED));
        assertThat(entry.getIdentifier(), is(IDENTIFIER));
        assertThat(entry.getTags(), is(TAGS));
        assertThat(entry.getCreated(), is(CREATED));
        assertThat(entry.getUpdated(), is(UPDATED));
        assertThat(entry.getLocked(), is(LOCKED));
        assertThat(entry.getUnlisted(), is(UNLISTED));
    }

    @Test
    public void testSerialization() throws Exception {
        final Entry entry = new Entry(LINK, TITLE, SUMMARY, CATEGORY, AUTHOR, CONTRIBUTOR, DEPRECATED, IDENTIFIER, TAGS,
                CREATED, UPDATED, LOCKED, UNLISTED);

        assertThat(entry, serializesToJson("/md/entry.json"));
    }

}