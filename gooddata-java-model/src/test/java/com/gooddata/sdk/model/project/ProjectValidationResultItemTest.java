/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class ProjectValidationResultItemTest {

    @Test
    public void testDeserialize() throws Exception {
        final ProjectValidationResultItem item = readObjectFromResource("/project/project-validationResultItem.json", ProjectValidationResultItem.class);

        assertThat(item.getValidation(), is(ProjectValidationType.PDM_TRANSITIVITY));
        assertThat(item.getLogs(), hasSize(3));
    }

}