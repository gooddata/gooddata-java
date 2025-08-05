/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import org.junit.jupiter.api.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ProjectValidationResultsTest {

    @Test
    public void testDeserialize() throws Exception {
        final ProjectValidationResults result = readObjectFromResource("/project/project-validationResults.json", ProjectValidationResults.class);

        assertThat(result.isError(), is(true));
        assertThat(result.isFatalError(), is(true));
        assertThat(result.isWarning(), is(true));
        assertThat(result.isValid(), is(false));
        assertThat(result.getResults(), notNullValue());
    }
}