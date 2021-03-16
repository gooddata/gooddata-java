/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project.model;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MaqlDdlLinksTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserialization() throws Exception {
        final MaqlDdlLinks maqlDdlLinks = readObjectFromResource("/model/maqlDdlLinks.json", MaqlDdlLinks.class);

        assertThat(maqlDdlLinks, is(notNullValue()));
        assertThat(maqlDdlLinks.getStatusUri(), is("/gdc/md/PROJECT_ID/tasks/123/status"));
    }
}