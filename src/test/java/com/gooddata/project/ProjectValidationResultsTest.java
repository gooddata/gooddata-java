/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ProjectValidationResultsTest {

    @Test
    public void testDeserialize() throws Exception {
        final ProjectValidationResults result = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/project/project-validationResults.json"), ProjectValidationResults.class);

        assertThat(result.isError(), is(true));
        assertThat(result.isFatalError(), is(true));
        assertThat(result.isWarning(), is(true));
        assertThat(result.isValid(), is(false));
        assertThat(result.getResults(), notNullValue());
    }
}