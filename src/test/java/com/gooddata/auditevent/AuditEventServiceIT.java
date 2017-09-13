/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.auditevent;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.GoodDataRestException;
import com.gooddata.account.Account;
import com.gooddata.collections.PageableList;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.gooddata.account.AccountServiceIT.ACCOUNT;
import static com.gooddata.account.AccountServiceIT.CURRENT_ACCOUNT_URI;
import static com.gooddata.util.ResourceUtils.readFromResource;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static net.jadler.Jadler.onRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuditEventServiceIT extends AbstractGoodDataIT {

    private AuditEventService service;

    @BeforeMethod
    public void setUp() throws Exception {
        service = gd.getAuditEventService();
    }

    @Test(expectedExceptions = AuditEventsForbiddenException.class)
    public void shouldThrowOnForbiddenDomain() {
        onRequest()
                .havingPathEqualTo("/gdc/domains/DOMAIN/auditEvents")
            .respond()
                .withStatus(SC_UNAUTHORIZED)
        ;

        service.listAuditEvents("DOMAIN");
    }

    @Test(expectedExceptions = AuditEventsForbiddenException.class)
    public void shouldThrowOnForbiddenAccount() {
        onRequest()
                .havingPathEqualTo("/gdc/account/profile/ID/auditEvents")
            .respond()
                .withStatus(SC_UNAUTHORIZED)
        ;
        final Account account = mock(Account.class);
        when(account.getId()).thenReturn("ID");

        service.listAuditEvents(account);
    }

    @Test(expectedExceptions = GoodDataRestException.class)
    public void shouldThrowOnUnknownError() {
        onRequest()
                .havingPathEqualTo("/gdc/account/profile/ID/auditEvents")
            .respond()
                .withStatus(SC_BAD_REQUEST)
        ;
        final Account account = mock(Account.class);
        when(account.getId()).thenReturn("ID");

        service.listAuditEvents(account);
    }

    @Test
    public void shouldReturnAuditEvents() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/domains/DOMAIN/auditEvents")
            .respond()
                .withBody(readFromResource("/auditevents/auditEvents.json"))
        ;
        onRequest()
                .havingPathEqualTo("/gdc/domains/DOMAIN/auditEvents")
                .havingParameterEqualTo("offset", "456")
            .respond()
                .withBody(readFromResource("/auditevents/auditEventsPage2.json"))
        ;

        final PageableList<AuditEvent> page1 = service.listAuditEvents("DOMAIN");
        assertThat(page1, hasSize(2));

        final PageableList<AuditEvent> page2 = service.listAuditEvents("DOMAIN", page1.getNextPage());
        assertThat(page2, hasSize(1));
    }

    @Test
    public void shouldReturnAuditEventsForUser() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(CURRENT_ACCOUNT_URI)
            .respond()
                .withBody(readFromResource(ACCOUNT))
                .withStatus(200);

        onRequest()
                .havingPathEqualTo("/gdc/account/profile/ID/auditEvents")
            .respond()
                .withBody(readFromResource("/auditevents/auditEvents.json"))
        ;

        final PageableList<AuditEvent> events = service.listAuditEvents();
        assertThat(events, hasSize(2));
    }
}