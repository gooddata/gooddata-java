/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.report;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TotalTest {

    @Test(expectedExceptions = UnsupportedOperationException.class,
            expectedExceptionsMessageRegExp = ".*\"unknownValue\".*")
    public void ofShouldThrowForUnknown() throws Exception {
        Total.of("unknownValue");
    }

    @Test
    public void testOf() throws Exception {
        for (Total total : Total.values()) {
            assertThat(Total.of(total.toString()), is(total));
        }
    }
}