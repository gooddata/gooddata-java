/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ObjTest {

    public static final String AUTHOR = "/gdc/account/profile/USER_ID";
    public static final String CONTRIBUTOR = "/gdc/account/profile/CONTRIBUTOR_USER_ID";
    public static final String CREATED = "2014-04-11 13:45:56";
    public static final String SUMMARY = "Obj summary";
    public static final String TITLE = "Obj title";
    public static final String UPDATED = "2014-04-11 13:45:57";
    public static final String CATEGORY = "attributeDisplayForm";
    public static final String TAGS = "TAG1 TAG2";
    public static final String URI = "/gdc/md/PROJECT_ID/obj/OBJ_ID";
    public static final String DEPRECATED = "0";
    public static final String IDENTIFIER = "attr.person.id.name";
    public static final Integer LOCKED = 0;
    public static final Integer UNLISTED = 1;

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
        assertThat(obj.getDeprecated(), is(DEPRECATED));
        assertThat(obj.getIdentifier(), is(IDENTIFIER));
        assertThat(obj.getLocked(), is(LOCKED));
        assertThat(obj.getUnlisted(), is(UNLISTED));
    }

    @Test
    public void testSerialization() throws Exception {
        final ConcreteObj obj = new ConcreteObj(
                new Meta(AUTHOR, CONTRIBUTOR, CREATED, UPDATED, SUMMARY, TITLE, CATEGORY, TAGS, URI, DEPRECATED,
                        IDENTIFIER, LOCKED, UNLISTED)
        );

        assertThat(obj, serializesToJson("/md/objCommon.json"));
    }

    public static class ConcreteObj extends AbstractObj {
        public ConcreteObj(@JsonProperty("meta") Meta meta) {
            super(meta);
        }
    }

}
