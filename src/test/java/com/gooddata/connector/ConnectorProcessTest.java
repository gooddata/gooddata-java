/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connector;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import static com.gooddata.connector.Status.Code.ERROR;
import static com.gooddata.connector.Status.Code.SYNCHRONIZED;
import static com.gooddata.connector.Status.Code.UPLOADING;
import static com.gooddata.connector.Status.Code.USER_ERROR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConnectorProcessTest {

    @Test
    public void testShouldDeserialize() throws Exception {
        final ConnectorProcess process = new ObjectMapper()
                .readValue(getClass().getResource("/connector/connector-process.json"), ConnectorProcess.class);

        assertThat(process, is(notNullValue()));
        assertThat(process.getStarted(), is("2014-05-30T07:50:15.000Z"));
        assertThat(process.getFinished(), is("2014-05-30T07:50:50.000Z"));
        assertThat(process.getStatus(), is(notNullValue()));
        assertThat(process.getStatus().getCode(), is(ERROR.name()));
        assertThat(process.getStatus().getDetail(), is("GDC-INTERNAL-ERROR"));
        assertThat(process.getStatus().getDescription(), is(nullValue()));
    }

    @Test
    public void testIsFinishedOnError() throws Exception {
        final ConnectorProcess process = new ConnectorProcess(new Status(ERROR.name(), "", ""), "", "");
        assertThat(process.isFinished(), is(true));
    }

    @Test
    public void testIsFinishedOnSynchronized() throws Exception {
        final ConnectorProcess process = new ConnectorProcess(new Status(SYNCHRONIZED.name(), "", ""), "", "");
        assertThat(process.isFinished(), is(true));
    }

    @Test
    public void testIsFinishedOnUploading() throws Exception {
        final ConnectorProcess process = new ConnectorProcess(new Status(UPLOADING.name(), "", ""), "", "");
        assertThat(process.isFinished(), is(false));
    }

    @Test
    public void testIsFinishedOnNullCode() throws Exception {
        final ConnectorProcess process = new ConnectorProcess(new Status(null, "", ""), "", "");
        assertThat(process.isFinished(), is(false));
    }

    @Test
    public void testIsFinishedOnUnknownCode() throws Exception {
        final ConnectorProcess process = new ConnectorProcess(new Status("unknown code", "", ""), "", "");
        assertThat(process.isFinished(), is(false));
    }

    @Test
    public void testIsFailedOnError() throws Exception {
        final ConnectorProcess process = new ConnectorProcess(new Status(ERROR.name(), "", ""), "", "");
        assertThat(process.isFailed(), is(true));
    }

    @Test
    public void testIsFailedOnUserError() throws Exception {
        final ConnectorProcess process = new ConnectorProcess(new Status(USER_ERROR.name(), "", ""), "", "");
        assertThat(process.isFailed(), is(true));
    }

    @Test
    public void testIsFailedOnSynchronized() throws Exception {
        final ConnectorProcess process = new ConnectorProcess(new Status(SYNCHRONIZED.name(), "", ""), "", "");
        assertThat(process.isFailed(), is(false));
    }

    @Test
    public void testIsFailedOnNullCode() throws Exception {
        final ConnectorProcess process = new ConnectorProcess(new Status(null, "", ""), "", "");
        assertThat(process.isFailed(), is(false));
    }

    @Test
    public void testIsFailedOnUnknownCode() throws Exception {
        final ConnectorProcess process = new ConnectorProcess(new Status("unknown code", "", ""), "", "");
        assertThat(process.isFailed(), is(false));
    }

}