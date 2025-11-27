/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ProjectValidationTypeTest {

    @Test
    public void testSerialize() throws Exception {
        final String myValidationType = OBJECT_MAPPER.writeValueAsString(new ProjectValidationType("myValidationType"));
        assertThat(myValidationType, is("\"myValidationType\""));
    }

    @Test
    public void testDeserialize() throws Exception {
        final ProjectValidationType myValidationType = OBJECT_MAPPER.readValue("\"myValidationType\"", ProjectValidationType.class);
        assertThat(myValidationType, notNullValue());
        assertThat(myValidationType.getValue(), is("myValidationType"));
    }

    @Test
    public void shouldVerifyEquals() throws Exception {
        EqualsVerifier.forClass(ProjectValidationType.class)
                .usingGetClass()
                .verify();
    }
}