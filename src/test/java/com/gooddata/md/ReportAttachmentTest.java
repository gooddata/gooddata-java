/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class ReportAttachmentTest {

    @Test
    public void shouldVerifyEquals() throws Exception {
        EqualsVerifier.forClass(ReportAttachment.class)
                .usingGetClass()
                .verify();
    }

}