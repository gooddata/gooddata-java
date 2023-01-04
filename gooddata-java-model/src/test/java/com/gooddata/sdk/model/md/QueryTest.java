/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class QueryTest {

    @Test
    public void testDeserialization() throws Exception {
        final Query query = readObjectFromResource("/md/query.json", Query.class);
        assertThat(query, is(notNullValue()));
        assertThat(query.getCategory(), is("MD::Query::Object"));
        assertThat(query.getTitle(), is("List of allTypes"));
        assertThat(query.getSummary(), is("Metadata Query Resources for project 'PROJ_ID'"));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final Query query = readObjectFromResource("/md/query.json", Query.class);

        assertThat(query.toString(), matchesPattern(Query.class.getSimpleName() + "\\[.*\\]"));
    }
}