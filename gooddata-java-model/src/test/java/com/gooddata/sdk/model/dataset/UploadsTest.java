/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class UploadsTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final Uploads uploads = readObjectFromResource("/dataset/uploads/uploads.json", Uploads.class);

        assertThat(uploads, notNullValue());
        assertThat(uploads.items(), not(Matchers.empty()));
        assertThat(uploads.items().size(), is(2));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final Uploads uploads = readObjectFromResource("/dataset/uploads/uploads.json", Uploads.class);

        assertThat(uploads.toString(), matchesPattern(Uploads.class.getSimpleName() + "\\[.*\\]"));
    }
}
