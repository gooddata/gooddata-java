/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.auditevent;

import com.gooddata.sdk.common.collections.CustomPageRequest;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.service.account.AccountService;

import org.springframework.web.reactive.function.client.WebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuditEventServiceTest {

    private AuditEventService service;

    @BeforeEach
    public void setUp() throws Exception {
        service = new AuditEventService(WebClient.builder().build(), mock(AccountService.class), new GoodDataSettings());
    }

    @Test
    public void shouldFailOnNullAccount() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.listAuditEvents((Account) null);
        }); 
        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("account")); 
    }

    @Test
    public void shouldFailOnNullAccountButPage() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.listAuditEvents((Account) null, new CustomPageRequest());
        });
        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("account"));
    }

    @Test
    public void shouldFailOnNullAccountIdButPage() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.listAuditEvents(mock(Account.class), new CustomPageRequest());
        });
        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("account.id"));
    }

    @Test
    public void shouldFailOnNullPageButAccount() {
        final Account account = mock(Account.class);
        when(account.getId()).thenReturn("123");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.listAuditEvents(account, null);
        });
        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("page"));
    }

    @Test
    public void shouldFailOnNullDomain() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.listAuditEvents((String) null);
        });
        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("domain"));
    }

    @Test
    public void shouldFailOnNullDomainButPage() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.listAuditEvents((String) null, new CustomPageRequest());
        });
        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("domain"));
    }

    @Test
    public void shouldFailOnNullPageButDomain() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.listAuditEvents("", null);
        });
        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("domain"));
    }
}