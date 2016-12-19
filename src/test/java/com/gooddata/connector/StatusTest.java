/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import org.testng.annotations.Test;

import static com.gooddata.connector.Status.Code.ERROR;
import static com.gooddata.connector.Status.Code.SYNCHRONIZED;
import static com.gooddata.connector.Status.Code.UPLOADING;
import static com.gooddata.connector.Status.Code.USER_ERROR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class StatusTest {

    @Test
    public void testIsFinishedOnError() throws Exception {
        assertThat(new Status(ERROR.name(), "", "").isFinished(), is(true));
    }

    @Test
    public void testIsFinishedOnSynchronized() throws Exception {
        assertThat(new Status(SYNCHRONIZED.name(), "", "").isFinished(), is(true));
    }

    @Test
    public void testIsFinishedOnUploading() throws Exception {
        assertThat(new Status(UPLOADING.name(), "", "").isFinished(), is(false));
    }

    @Test
    public void testIsFinishedOnNullCode() throws Exception {
        assertThat(new Status(null, "", "").isFinished(), is(false));
    }

    @Test
    public void testIsFinishedOnUnknownCode() throws Exception {
        assertThat(new Status("unknown code", "", "").isFinished(), is(false));
    }

    @Test
    public void testIsFailedOnError() throws Exception {
        assertThat(new Status(ERROR.name(), "", "").isFailed(), is(true));
    }

    @Test
    public void testIsFailedOnUserError() throws Exception {
        assertThat(new Status(USER_ERROR.name(), "", "").isFailed(), is(true));
    }

    @Test
    public void testIsFailedOnSynchronized() throws Exception {
        assertThat(new Status(SYNCHRONIZED.name(), "", "").isFailed(), is(false));
    }

    @Test
    public void testIsFailedOnNullCode() throws Exception {
        assertThat(new Status(null, "", "").isFailed(), is(false));
    }

    @Test
    public void testIsFailedOnUnknownCode() throws Exception {
        assertThat(new Status("unknown code", "", "").isFailed(), is(false));
    }

    @Test
    public void testToStringFormat() {
        final Status status = new Status("unknown code", "", "");

        assertThat(status.toString(), matchesPattern(Status.class.getSimpleName() + "\\[.*\\]"));
    }

}