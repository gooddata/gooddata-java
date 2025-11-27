/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ObjTest {

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

    @Test
    public void testDeserialization() throws Exception {
        final AbstractObj obj = readObjectFromResource("/md/objCommon.json", ConcreteObj.class);

        assertThat(obj, is(notNullValue()));
        assertThat(obj.getId(), is(OBJ_ID));
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
        assertThat(obj.getFlags(), nullValue());
    }

    @Test
    public void testSerialization() throws Exception {
        final ConcreteObj obj = new ConcreteObj(
                new Meta(AUTHOR, CONTRIBUTOR, CREATED, UPDATED, SUMMARY, TITLE, CATEGORY, TAGS, URI, IDENTIFIER,
                        DEPRECATED, null, LOCKED, UNLISTED, null, null)
        );


        assertThat(obj, jsonEquals(resource("md/objCommon.json")));
    }

    public static class ConcreteObj extends AbstractObj {
        public ConcreteObj(@JsonProperty("meta") Meta meta) {
            super(meta);
        }
    }

}
