/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.auditevent;

import com.gooddata.sdk.common.collections.CustomPageRequest;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.service.account.AccountService;
import com.gooddata.sdk.common.collections.PageRequest;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuditEventServiceTest {

    private AuditEventService service;

    @BeforeMethod
    public void setUp() throws Exception {
        service = new AuditEventService(new RestTemplate(), mock(AccountService.class), new GoodDataSettings());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*account.*")
    public void shouldFailOnNullAccount() throws Exception {
        service.listAuditEvents((Account) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*account.*")
    public void shouldFailOnNullAccountButPage() throws Exception {
        service.listAuditEvents((Account) null, new CustomPageRequest());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*account.id.*")
    public void shouldFailOnNullAccountIdButPage() throws Exception {
        service.listAuditEvents(mock(Account.class), new CustomPageRequest());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*page.*")
    public void shouldFailOnNullPageButAccount() throws Exception {
        final Account account = mock(Account.class);
        when(account.getId()).thenReturn("123");
        service.listAuditEvents(account, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*domain.*")
    public void shouldFailOnNullDomain() throws Exception {
        service.listAuditEvents((String) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*domain.*")
    public void shouldFailOnNullDomainButPage() throws Exception {
        service.listAuditEvents((String) null, new CustomPageRequest());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*domain.*")
    public void shouldFailOnNullPageButDomain() throws Exception {
        service.listAuditEvents("", null);
    }
}