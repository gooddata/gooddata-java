/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.auditevent;

import com.gooddata.sdk.service.AbstractGoodDataIT;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.service.account.AccountServiceIT;
import com.gooddata.sdk.model.auditevent.AuditEvent;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
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

        final Page<AuditEvent> page1 = service.listAuditEvents("DOMAIN");
        assertThat(page1.getPageItems(), hasSize(2));

        final Page<AuditEvent> page2 = service.listAuditEvents("DOMAIN", page1.getNextPage());
        assertThat(page2.getPageItems(), hasSize(1));
    }

    @Test
    public void shouldReturnAuditEventsForUser() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(AccountServiceIT.CURRENT_ACCOUNT_URI)
            .respond()
                .withBody(readFromResource(AccountServiceIT.ACCOUNT))
                .withStatus(200);

        onRequest()
                .havingPathEqualTo("/gdc/account/profile/ID/auditEvents")
            .respond()
                .withBody(readFromResource("/auditevents/auditEvents.json"))
        ;

        final Page<AuditEvent> events = service.listAuditEvents();
        assertThat(events.getPageItems(), hasSize(2));
    }
}
