/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.auditevent;

import com.gooddata.sdk.service.AbstractGoodDataAT;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.model.auditevent.AuditEvent;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class AuditEventServiceAT extends AbstractGoodDataAT {

    @Test
    public void shouldListEventsForCurrentUser() throws Exception {
        final Page<AuditEvent> events = AbstractGoodDataAT.gd.getAuditEventService().listAuditEvents();
        assertThat(events, is(notNullValue()));
    }

    @Test(groups = "isolated_domain")
    public void shouldListEventsForDomain() throws Exception {
        final Page<AuditEvent> events = AbstractGoodDataAT.gd.getAuditEventService().listAuditEvents("default");
        assertThat(events, is(notNullValue()));
    }
}