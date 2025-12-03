/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;


import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertNull;

public class ReloadTest {

    private static final int RELOAD_ID = 123;
    private static final String PROCESS_ID = "processId";

    private static final String SELF_LINK = "/self/link";
    private static final String INTEGRATION_LINK = "/integration/link";
    private static final String PROCESS_LINK = "/process/link";

    private static final long CHATS_START_TIME = 1112L;
    private static final long AGENT_TIMELINE_START_TIME = 0L;

    private Reload reload;
    private Map<String, Long> startTimes;
    private Map<String, String> links;

    @BeforeMethod
    public void setUp() {
        startTimes = new HashMap<>();
        startTimes.put(Reload.CHATS_START_TIME_PROPERTY, CHATS_START_TIME);
        startTimes.put(Reload.AGENT_TIMELINE_START_TIME_PROPERTY, AGENT_TIMELINE_START_TIME);

        links = new HashMap<>();
        links.put("self", SELF_LINK);
        links.put("integration", INTEGRATION_LINK);
        links.put("process", PROCESS_LINK);

        reload = new Reload(RELOAD_ID, startTimes, Reload.STATUS_RUNNING, PROCESS_ID, links);
    }

    @Test
    public void testGetId() {
        assertThat(reload.getId(), is(RELOAD_ID));
    }

    @Test
    public void testGetStartTimes() {
        assertThat(reload.getStartTimes(), is(startTimes));
    }

    @Test
    public void testGetChatsStartTime() {
        assertThat(reload.getChatsStartTime(), is(CHATS_START_TIME));
    }

    @Test
    public void testGetAgentTimelineStartTime() {
        assertThat(reload.getAgentTimelineStartTime(), is(AGENT_TIMELINE_START_TIME));
    }

    @Test
    public void testGetStatus() {
        assertThat(reload.getStatus(), is(Reload.STATUS_RUNNING));
    }

    @Test
    public void testGetProcessId() {
        assertThat(reload.getProcessId(), is(PROCESS_ID));
    }

    @Test
    public void testGetLinks() {
        assertThat(reload.getLinks(), is(links));
    }

    @Test
    public void testGetUri() {
        assertThat(reload.getUri().get(), is(SELF_LINK));
    }

    @Test
    public void testGetProcessUri() {
        assertThat(reload.getProcessUri().get(), is(PROCESS_LINK));
    }

    @Test
    public void testGetIntegrationUri() {
        assertThat(reload.getIntegrationUri().get(), is(INTEGRATION_LINK));
    }

    @Test
    public void constructor2nd() {
        reload = new Reload(startTimes);
        assertNull(reload.getId());
        assertThat(reload.getStartTimes(), is(startTimes));
        assertThat(reload.getChatsStartTime(), is(CHATS_START_TIME));
        assertThat(reload.getAgentTimelineStartTime(), is(AGENT_TIMELINE_START_TIME));
        assertNull(reload.getStatus());
        assertNull(reload.getProcessId());
        assertNull(reload.getLinks());
        assertFalse(reload.getUri().isPresent());
        assertFalse(reload.getProcessUri().isPresent());
        assertFalse(reload.getIntegrationUri().isPresent());
    }
}