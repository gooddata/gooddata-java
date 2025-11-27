/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.gdc;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class LinkEntriesTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserialization() throws Exception {
        final LinkEntries linkEntries = readObjectFromResource("/gdc/linkEntries.json", LinkEntries.class);

        assertThat(linkEntries, is(notNullValue()));
        assertThat(linkEntries.getEntries(), hasSize(1));
        assertThat(linkEntries.getEntries().get(0), is(notNullValue()));
        assertThat(linkEntries.getEntries().get(0).getUri(), is("URI"));
        assertThat(linkEntries.getEntries().get(0).getCategory(), is("tasks-status"));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final LinkEntries linkEntries = readObjectFromResource("/gdc/linkEntries.json", LinkEntries.class);

        assertThat(linkEntries.toString(), matchesPattern(LinkEntries.class.getSimpleName() + "\\[.*\\]"));
    }
}
