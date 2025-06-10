/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import org.junit.jupiter.api.Test;

import static com.gooddata.sdk.model.project.ProjectValidationType.*;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ProjectValidationsTest {

    @Test
    public void testSerialize() throws Exception {
        assertThat(new ProjectValidations(PDM_VS_DWH, METRIC_FILTER, PDM_TRANSITIVITY, LDM, INVALID_OBJECTS, PDM_ELEM,
                        PDM_PK_FK_CONSISTENCY),
                jsonEquals(resource("project/project-validate.json")).when(IGNORING_ARRAY_ORDER));
    }

    @Test
    public void testDeserialize() throws Exception {
        final ProjectValidations validations = readObjectFromResource("/project/project-validationAvail.json",
                ProjectValidations.class);

        assertThat(validations, is(notNullValue()));
        assertThat(validations.getValidations(), hasItems(PDM_VS_DWH, METRIC_FILTER, PDM_TRANSITIVITY, LDM,
                INVALID_OBJECTS, PDM_ELEM, PDM_PK_FK_CONSISTENCY));
    }
}