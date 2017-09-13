/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.auditevent;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.collections.PageableList;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class AuditEventServiceAT extends AbstractGoodDataAT {

    @Test
    public void shouldListEventsForCurrentUser() throws Exception {
        final PageableList<AuditEvent> events = gd.getAuditEventService().listAuditEvents();
        assertThat(events, is(notNullValue()));
    }

    @Test(groups = "isolated_domain")
    public void shouldListEventsForDomain() throws Exception {
        final PageableList<AuditEvent> events = gd.getAuditEventService().listAuditEvents("default");
        assertThat(events, is(notNullValue()));
    }
}