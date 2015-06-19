/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.featureflag;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProjectFeatureFlagTest {

    @Test
    public void testDefaultValue() throws Exception {
        final ProjectFeatureFlag flag = new ProjectFeatureFlag("test");
        assertThat(flag.isEnabled(), is(true));
    }

    @Test
    public void testCustomValue() throws Exception {
        final ProjectFeatureFlag flag = new ProjectFeatureFlag("test", false);
        assertThat(flag.isEnabled(), is(false));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEmptyName() throws Exception {
        new ProjectFeatureFlag(" ");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEmptyNameWithValue() throws Exception {
        new ProjectFeatureFlag(" ", false);
    }
}