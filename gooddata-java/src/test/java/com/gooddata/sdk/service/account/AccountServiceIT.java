/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.account;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.account.Account;

import com.gooddata.sdk.model.account.SeparatorSettings;
import com.gooddata.sdk.service.AbstractGoodDataIT;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.util.Collections;
import java.util.List;

import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static com.gooddata.sdk.common.util.ResourceUtils.readStringFromResource;
import static com.gooddata.sdk.model.account.Account.AuthenticationMode.SSO;
import static net.jadler.Jadler.onRequest;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountServiceIT extends AbstractGoodDataIT {

    private static final String DOMAIN = "default";
    private static final String CREATE_ACCOUNT = "/account/create-account.json";
    public static final String ACCOUNT = "/account/account.json";
    private static final String ACCOUNT_BY_EMAIL = "/account/account-by-email.json";
    private static final String ACCOUNT_BY_EMAIL_EMPTY_RESPONSE = "/account/account-by-email-empty-response.json";
    private static final String ACCOUNT_UPDATE = "/account/update-account.json";
    private static final String SEPARATORS = "/account/separators.json";
    private static final String ACCOUNT_ID = "ID";
    private static final String ACCOUNT_URI = AccountService.ACCOUNT_TEMPLATE.expand(ACCOUNT_ID).toString();
    private static final String LOGIN_EMAIL = "example@company.com";
    private static final String ACCOUNT_BY_EMAIL_URI = AccountService.ACCOUNTS_TEMPLATE
        .expand(DOMAIN).toString();


    private static final String SEPARATORS_URI = AccountService.SEPARATORS_TEMPLATE.expand(ACCOUNT_ID).toString();
    public static final String CURRENT_ACCOUNT_URI = AccountService.ACCOUNT_TEMPLATE.expand(Account.CURRENT_ID).toString();
    private static final String LOGOUT_CURRENT = AccountService.LOGIN_TEMPLATE.expand(ACCOUNT_ID).toString();


    private static Account account;
    private static Account createAccount;

    @BeforeAll
    public static void init() {
        account = readObjectFromResource(ACCOUNT, Account.class);
        createAccount = readObjectFromResource(CREATE_ACCOUNT, Account.class);
    }

    @Test
    public void shouldCreateAccount() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(AccountService.ACCOUNTS_TEMPLATE.expand(DOMAIN).toString())
                .respond()
                .withBody("{\"uri\": \"" + ACCOUNT_URI + "\"}")
                .withStatus(201);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ACCOUNT_URI)
                .respond()
                .withBody(readFromResource(ACCOUNT))
                .withStatus(200);

        final Account created = gd.getAccountService().createAccount(createAccount, DOMAIN);

        assertThat(created, notNullValue());
        assertThat(created.getFirstName(), is("Blah"));
    }

    @Test
    public void shouldFailToCreateAccount() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(AccountService.ACCOUNTS_TEMPLATE.expand(DOMAIN).toString())
                .respond()
                .withBody("")
                .withStatus(400);

        assertThrows(GoodDataException.class, () -> {
            gd.getAccountService().createAccount(createAccount, DOMAIN);
        });
    }

    @Test
    public void shouldRemoveAccount() {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(ACCOUNT_URI)
                .respond()
                .withStatus(204);

        gd.getAccountService().removeAccount(account);
    }

    @Test
    public void shouldFailToFindAccountForRemoval() {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(ACCOUNT_URI)
                .respond()
                .withStatus(404);

        assertThrows(AccountNotFoundException.class, () -> {
            gd.getAccountService().removeAccount(account);
        });
    }

    @Test
    public void shouldGetCurrentAccount() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(CURRENT_ACCOUNT_URI)
                .respond()
                .withBody(readFromResource(ACCOUNT))
                .withStatus(200);

        final Account current = gd.getAccountService().getCurrent();

        assertThat(current, notNullValue());
        assertThat(current.getFirstName(), is("Blah"));
    }

    @Test
    public void shouldFailToGetCurrentAccount() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(CURRENT_ACCOUNT_URI)
                .respond()
                .withStatus(400);

        assertThrows(GoodDataException.class, () -> {
            gd.getAccountService().getCurrent();
        });
    }

    @Test
    public void shouldFailToFindCurrentAccount() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(CURRENT_ACCOUNT_URI)
                .respond()
                .withStatus(404);

        assertThrows(AccountNotFoundException.class, () -> {
            gd.getAccountService().getCurrent();
        });
    }

    @Test
    public void shouldLogout() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(CURRENT_ACCOUNT_URI)
                .respond()
                .withBody(readFromResource(ACCOUNT))
                .withStatus(200);
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(LOGOUT_CURRENT)
                .respond()
                .withBody(readFromResource(ACCOUNT))
                .withStatus(204);

        gd.getAccountService().logout();
    }

    @Test
    public void shouldFailToLogout() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(CURRENT_ACCOUNT_URI)
                .respond()
                .withBody(readFromResource(ACCOUNT))
                .withStatus(200);
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(LOGOUT_CURRENT)
                .respond()
                .withStatus(400);

        assertThrows(GoodDataException.class, () -> {
            gd.getAccountService().logout();
        });
    }

    @Test
    public void shouldGetAccount() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ACCOUNT_URI)
                .respond()
                .withBody(readFromResource(ACCOUNT))
                .withStatus(200);

        final Account created = gd.getAccountService().getAccountById(ACCOUNT_ID);

        assertThat(created, notNullValue());
        assertThat(created.getFirstName(), is("Blah"));
    }

    @Test
    public void shouldGetAccountByEmail() {
        onRequest()
            .havingMethodEqualTo("GET")
            .havingPathEqualTo(ACCOUNT_BY_EMAIL_URI)
            .havingQueryStringEqualTo("login=" + LOGIN_EMAIL)
            .respond()
            .withBody(readFromResource(ACCOUNT_BY_EMAIL))
            .withStatus(200);

        final Account loaded = gd.getAccountService().getAccountByLogin(LOGIN_EMAIL, DOMAIN);
        assertThat(loaded.getFirstName(), is("John"));
    }

    @Test
    public void shouldGetEmptyPageWhenAccountByEmailDoesNotExist() {
        onRequest()
            .havingMethodEqualTo("GET")
            .havingPathEqualTo(ACCOUNT_BY_EMAIL_URI)
            .havingQueryStringEqualTo("login=wrong@email.com")
            .respond()
            .withBody(readFromResource(ACCOUNT_BY_EMAIL_EMPTY_RESPONSE))
            .withStatus(200);

        assertThrows(AccountNotFoundException.class, () -> {
            gd.getAccountService().getAccountByLogin("wrong@email.com", DOMAIN);
        });
    }

    @Test
    public void shouldFailToFindAccount() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ACCOUNT_URI)
                .respond()
                .withStatus(404);

        assertThrows(AccountNotFoundException.class, () -> {
            gd.getAccountService().getAccountById(ACCOUNT_ID);
        }); 
    }

    @Test
    public void shouldFailOnUpdateNonExistentAccount() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ACCOUNT_URI)
                .respond()
                .withStatus(404);

        assertThrows(AccountNotFoundException.class, () -> {
            gd.getAccountService().updateAccount(account);
        });
    }

    @Test
    public void shouldUpdateAccount() {
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo(ACCOUNT_URI)
                .havingBody(jsonEquals(readStringFromResource(ACCOUNT_UPDATE)))
                .respond()
                .withStatus(200);

        final Account toBeUpdated = readObjectFromResource(ACCOUNT, Account.class);

        final String newFirstName = "newFirstName2";
        final String newEmail = "fake2@gooddata.com";
        final String newPass = "password2";
        final String newLastName = "Muhehe2";
        final List<String> authenticationModes = Collections.singletonList(SSO.toString());

        toBeUpdated.setFirstName(newFirstName);
        toBeUpdated.setEmail(newEmail);
        toBeUpdated.setPassword(newPass);
        toBeUpdated.setVerifyPassword(newPass);
        toBeUpdated.setLastName(newLastName);
        toBeUpdated.setAuthenticationModes(authenticationModes);

        gd.getAccountService().updateAccount(toBeUpdated);
    }

    @Test
    public void shouldGetAccountSeparatorSettings() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(SEPARATORS_URI)
                .respond()
                .withStatus(200)
                .withBody(readFromResource(SEPARATORS));

        final Account account = readObjectFromResource(ACCOUNT, Account.class);

        final SeparatorSettings separators = gd.getAccountService().getSeparatorSettings(account);

        assertThat(separators, notNullValue());
    }

    @Test
    public void shouldFailGetAccountSeparatorSettings() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(SEPARATORS_URI)
                .respond()
                .withStatus(404);

        final Account account = readObjectFromResource(ACCOUNT, Account.class);

        assertThrows(GoodDataException.class, () -> {
            gd.getAccountService().getSeparatorSettings(account);
        });
    }
}
