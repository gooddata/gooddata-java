/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import org.testng.annotations.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.project.ProjectValidationType.INVALID_OBJECTS;
import static com.gooddata.project.ProjectValidationType.LDM;
import static com.gooddata.project.ProjectValidationType.METRIC_FILTER;
import static com.gooddata.project.ProjectValidationType.PDM_ELEM;
import static com.gooddata.project.ProjectValidationType.PDM_PK_FK_CONSISTENCY;
import static com.gooddata.project.ProjectValidationType.PDM_TRANSITIVITY;
import static com.gooddata.project.ProjectValidationType.PDM_VS_DWH;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ProjectValidationsTest {

    @Test
    public void testSerialize() throws Exception {
        assertThat(new ProjectValidations(PDM_VS_DWH, METRIC_FILTER, PDM_TRANSITIVITY, LDM,
                INVALID_OBJECTS, PDM_ELEM, PDM_PK_FK_CONSISTENCY),
                serializesToJson("/project/project-validate.json"));
    }

    @Test
    public void testDeserialize() throws Exception {
        final ProjectValidations validations = readObjectFromResource("/project/project-validationAvail.json", ProjectValidations.class);

        assertThat(validations, is(notNullValue()));
        assertThat(validations.getValidations(), hasItems(PDM_VS_DWH, METRIC_FILTER, PDM_TRANSITIVITY, LDM,
                INVALID_OBJECTS, PDM_ELEM, PDM_PK_FK_CONSISTENCY));
    }
}