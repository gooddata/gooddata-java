/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class MetaTest {

    public static final String AUTHOR = "/gdc/account/profile/USER_ID";
    public static final String CONTRIBUTOR = "/gdc/account/profile/CONTRIBUTOR_USER_ID";
    public static final ZonedDateTime CREATED = LocalDateTime.of(2014, 4, 11, 13, 45, 56).atZone(UTC);
    public static final String SUMMARY = "Obj summary";
    public static final String TITLE = "Obj title";
    public static final ZonedDateTime UPDATED = LocalDateTime.of(2014, 4, 11, 13, 45, 57).atZone(UTC);
    public static final String CATEGORY = "attributeDisplayForm";
    public static final Set<String> TAGS = new LinkedHashSet<>(asList("TAG1", "TAG2"));
    public static final String OBJ_ID = "OBJ_ID";
    public static final String URI = "/gdc/md/PROJECT_ID/obj/" + OBJ_ID;
    public static final boolean DEPRECATED = false;
    public static final String IDENTIFIER = "attr.person.id.name";
    public static final boolean LOCKED = false;
    public static final boolean UNLISTED = true;
    public static final boolean PRODUCTION = true;
    public static final boolean SHARED_WITH_SOMEONE = false;
    public static final Set<String> FLAGS = new LinkedHashSet<>(asList("preloaded", "strictAccessControl"));

    @Test
    public void testDeserialization() throws Exception {
        final Meta meta = readObjectFromResource("/md/meta.json", Meta.class);
        assertThat(meta, is(notNullValue()));
        assertThat(meta.getId(), is(OBJ_ID));
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
        assertThat(meta.getFlags(), is(FLAGS));
    }

    @Test
    public void testSerialization() throws Exception {
        final Meta meta = new Meta(AUTHOR, CONTRIBUTOR, CREATED, UPDATED, SUMMARY, TITLE, CATEGORY, TAGS, URI, IDENTIFIER,
                DEPRECATED, PRODUCTION, LOCKED, UNLISTED, SHARED_WITH_SOMEONE, FLAGS);

        assertThat(meta, jsonEquals(resource("md/meta.json")));
    }

    @Test
    public void testDeserializationWithFlags() throws Exception {
        final Meta meta = readObjectFromResource("/md/meta-withFlags.json", Meta.class);
        assertThat(meta, is(notNullValue()));
        assertThat(meta.getFlags(), is(FLAGS));
    }

    @Test
    public void testSerializationWithFlags() throws Exception {
        final Meta meta = new Meta(AUTHOR, CONTRIBUTOR, CREATED, UPDATED, SUMMARY, TITLE, CATEGORY, TAGS, URI, IDENTIFIER,
                DEPRECATED, PRODUCTION, LOCKED, UNLISTED, SHARED_WITH_SOMEONE, FLAGS);

        assertThat(meta, jsonEquals(resource("md/meta-withFlags.json")));
    }

    @Test
    public void testToStringFormat() {
        final Meta meta = new Meta(AUTHOR, CONTRIBUTOR, CREATED, UPDATED, SUMMARY, TITLE, CATEGORY, TAGS, URI, IDENTIFIER,
                DEPRECATED, PRODUCTION, LOCKED, UNLISTED, SHARED_WITH_SOMEONE, FLAGS);

        assertThat(meta.toString(), matchesPattern(Meta.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final Meta meta = new Meta(AUTHOR, CONTRIBUTOR, CREATED, UPDATED, SUMMARY, TITLE, CATEGORY, TAGS, URI, IDENTIFIER,
                DEPRECATED, PRODUCTION, LOCKED, UNLISTED, SHARED_WITH_SOMEONE, FLAGS);
        final Meta deserialized = SerializationUtils.roundtrip(meta);

        assertThat(deserialized, jsonEquals(meta));
    }
}
