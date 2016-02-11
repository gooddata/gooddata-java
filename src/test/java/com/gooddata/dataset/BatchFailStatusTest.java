/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

public class BatchFailStatusTest {
    @Test
    public void testParser() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/dataset/batchFailStatus1.json");
        final BatchFailStatus value = new ObjectMapper().readValue(stream, BatchFailStatus.class);
        assertThat(value, is(notNullValue()));
        assertThat(value.getStatus(),is("ERROR"));
        assertThat(value.getMessages(),is(Matchers.<String>empty()));
        assertThat(value.getDate(),is(new DateTime(2016,2,1,10,12,9, DateTimeZone.UTC)));
        assertThat(value.getFailStatuses(),hasSize(2));
    }

    @Test
    public void testParser2() throws IOException {
        final InputStream stream = getClass().getResourceAsStream("/dataset/batchFailStatus2.json");
        final BatchFailStatus value = new ObjectMapper().readValue(stream, BatchFailStatus.class);
        assertThat(value,is(notNullValue()));
        assertThat(value.getStatus(),is("ERROR"));
        assertThat(value.getMessages(),containsInAnyOrder("test1","test2"));
        assertThat(value.getDate(),is(new DateTime(2016,1,1,10,11,15, DateTimeZone.UTC)));
        assertThat(value.getFailStatuses(),is(Matchers.<FailStatus>empty()));
    }
}