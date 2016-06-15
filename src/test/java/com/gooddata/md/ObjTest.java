/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.gooddata.JsonMatchers.serializesToJson;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ObjTest {

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

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/objCommon.json");
        final AbstractObj obj = new ObjectMapper().readValue(stream, ConcreteObj.class);

        assertThat(obj, is(notNullValue()));
        assertThat(obj.getAuthor(), is(AUTHOR));
        assertThat(obj.getContributor(), is(CONTRIBUTOR));
        assertThat(obj.getCreated(), is(CREATED));
        assertThat(obj.getSummary(), is(SUMMARY));
        assertThat(obj.getTitle(), is(TITLE));
        assertThat(obj.getUpdated(), is(UPDATED));
        assertThat(obj.getCategory(), is(CATEGORY));
        assertThat(obj.getTags(), is(TAGS));
        assertThat(obj.getUri(), is(URI));
        assertThat(obj.isDeprecated(), is(DEPRECATED));
        assertThat(obj.getIdentifier(), is(IDENTIFIER));
        assertThat(obj.isLocked(), is(LOCKED));
        assertThat(obj.isUnlisted(), is(UNLISTED));
    }

    @Test
    public void testSerialization() throws Exception {
        final ConcreteObj obj = new ConcreteObj(
                new Meta(AUTHOR, CONTRIBUTOR, CREATED, UPDATED, SUMMARY, TITLE, CATEGORY, TAGS, URI, IDENTIFIER,
                        DEPRECATED, null, LOCKED, UNLISTED, null)
        );

        assertThat(obj, serializesToJson("/md/objCommon.json"));
    }

    public static class ConcreteObj extends AbstractObj {
        public ConcreteObj(@JsonProperty("meta") Meta meta) {
            super(meta);
        }
    }

}
