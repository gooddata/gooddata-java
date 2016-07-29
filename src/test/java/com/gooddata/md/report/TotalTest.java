/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md.report;

import org.testng.annotations.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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