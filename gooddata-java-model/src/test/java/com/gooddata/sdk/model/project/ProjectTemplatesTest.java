/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class ProjectTemplatesTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final ProjectTemplates templates = readObjectFromResource("/project/project-templates.json", ProjectTemplates.class);

        assertThat(templates, is(notNullValue()));
        assertThat(templates.getTemplatesInfo(), is(notNullValue()));
        assertThat(templates.getTemplatesInfo(), hasSize(1));
    }
}