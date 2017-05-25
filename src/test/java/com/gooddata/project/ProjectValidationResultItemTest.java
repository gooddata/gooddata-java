/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class ProjectValidationResultItemTest {

    @Test
    public void testDeserialize() throws Exception {
        final ProjectValidationResultItem item = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/project/project-validationResultItem.json"), ProjectValidationResultItem.class);

        assertThat(item.getValidation(), is(ProjectValidationType.PDM_TRANSITIVITY));
        assertThat(item.getLogs(), hasSize(3));
    }

}