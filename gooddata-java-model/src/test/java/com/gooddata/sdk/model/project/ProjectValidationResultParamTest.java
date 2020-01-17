/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.core.type.TypeReference;
import org.testng.annotations.Test;

import java.util.List;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

public class ProjectValidationResultParamTest {

    @Test
    public void testDeser() throws Exception {
        final List<ProjectValidationResultParam> result = OBJECT_MAPPER
                .readValue(readFromResource("/project/project-validationResultParam.json"),
                        new TypeReference<List<ProjectValidationResultParam>>() { });

        assertThat(result, hasItem(sameBeanAs(new ProjectValidationResultStringParam("report"))));
        assertThat(result, hasItem(sameBeanAs(new ProjectValidationResultObjectParam("Historical backlog", "/gdc/md/d45dlwq6fixqsgbukrlnir1qsw8y44q6/obj/41886"))));
        assertThat(result, hasItem(sameBeanAs(new ProjectValidationResultSliElParam(asList("4761"), asList("Deleted")))));
        assertThat(result, hasItem(sameBeanAs(new ProjectValidationResultGdcTimeElParam(asList("4762")))));
    }
}