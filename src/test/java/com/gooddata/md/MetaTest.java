/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.gooddata.JsonMatchers.serializesToJson;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MetaTest {

    public static final String AUTHOR = "/gdc/account/profile/USER_ID";
    public static final String CONTRIBUTOR = "/gdc/account/profile/CONTRIBUTOR_USER_ID";
    public static final DateTime CREATED = new DateTime(2014, 4, 11, 13, 45, 56, DateTimeZone.UTC);
    public static final String SUMMARY = "Obj summary";
    public static final String TITLE = "Obj title";
    public static final DateTime UPDATED = new DateTime(2014, 4, 11, 13, 45, 57, DateTimeZone.UTC);
    public static final String CATEGORY = "attributeDisplayForm";
    public static final Set<String> TAGS = new LinkedHashSet<>(asList("TAG1", "TAG2"));
    public static final String URI = "/gdc/md/PROJECT_ID/obj/OBJ_ID";
    public static final boolean DEPRECATED = false;
    public static final String IDENTIFIER = "attr.person.id.name";
    public static final boolean LOCKED = false;
    public static final boolean UNLISTED = true;
    public static final boolean PRODUCTION = true;
    public static final boolean SHARED_WITH_SOMEONE = false;

    @Test
    public void testDeserialization() throws Exception {
        final Meta meta = new ObjectMapper().readValue(getClass().getResourceAsStream("/md/meta.json"), Meta.class);
        assertThat(meta, is(notNullValue()));
        assertThat(meta.getAuthor(), is(AUTHOR));
        assertThat(meta.getContributor(), is(CONTRIBUTOR));
        assertThat(meta.getCreated(), is(CREATED));
        assertThat(meta.getSummary(), is(SUMMARY));
        assertThat(meta.getTitle(), is(TITLE));
        assertThat(meta.getUpdated(), is(UPDATED));
        assertThat(meta.getCategory(), is(CATEGORY));
        assertThat(meta.getTags(), is(TAGS));
        assertThat(meta.getUri(), is(URI));
        assertThat(meta.isDeprecated(), is(DEPRECATED));
        assertThat(meta.getIdentifier(), is(IDENTIFIER));
        assertThat(meta.isLocked(), is(LOCKED));
        assertThat(meta.isUnlisted(), is(UNLISTED));
        assertThat(meta.isProduction(), is(PRODUCTION));
        assertThat(meta.isSharedWithSomeone(), is(SHARED_WITH_SOMEONE));
    }

    @Test
    public void testSerialization() throws Exception {
        final Meta meta = new Meta(AUTHOR, CONTRIBUTOR, CREATED, UPDATED, SUMMARY, TITLE, CATEGORY, TAGS, URI, IDENTIFIER,
                DEPRECATED, PRODUCTION, LOCKED, UNLISTED, SHARED_WITH_SOMEONE);

        assertThat(meta, serializesToJson("/md/meta.json"));
    }

}
