/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.testng.annotations.Test;

public class ProjectValidationResultGdcTimeElParamTest {

    @Test
    public void shouldVerifyEquals() throws Exception {
        EqualsVerifier.forClass(ProjectValidationResultGdcTimeElParam.class)
                .usingGetClass()
                .verify();
    }

}