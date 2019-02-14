/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.account;

import com.gooddata.sdk.service.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.model.gdc.UriResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

/**
 * Service to access and manipulate account.
 */
public class AccountService extends AbstractService {

    /**
     * Constructs service for GoodData account management.
     *
     * @param restTemplate RESTful HTTP Spring template
     * @param settings settings
     */
    public AccountService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        super(restTemplate, settings);
    }

    /**
     * Gets current account of logged user.
     *
     * @return current account
     * @throws com.gooddata.GoodDataException when current account can't be accessed.
     */
    public Account getCurrent() {
        return getAccountById(Account.CURRENT_ID);
    }

    /**
     * Performs user logout.
     *
     * @throws com.gooddata.GoodDataException when logout failed.
     */
    public void logout() {
        try {
            final String id = getCurrent().getId();
            restTemplate.delete(Account.LOGIN_URI, id);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to logout", e);
        }
    }

    /**
     * Creates new account in given organization (domain).
     * Only domain admin is allowed create new accounts! This means rest request has to authorized as domain admin.
     * @param account to create
     * @param organizationName (domain) in which account should be created
     * @return new account
     * @throws GoodDataException when account can't be created
     */
    public Account createAccount(Account account, String organizationName) {
        notNull(account, "account");
        notEmpty(organizationName, "organizationName");

        try {
            final UriResponse uriResponse = restTemplate.postForObject(Account.ACCOUNTS_URI, account, UriResponse.class, organizationName);
            return getAccountByUri(uriResponse.getUri());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to create account", e);
        }
    }

    /**
     * Delete given account
     * @param account to remove
     * @throws AccountNotFoundException when given account wasn't found
     * @throws GoodDataException when account can't be removed for other reason
     */
    public void removeAccount(final Account account) {
        notNull(account, "account");
        notNull(account.getUri(), "account.uri");

        try {
            restTemplate.delete(account.getUri());
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new AccountNotFoundException(account.getUri(), e);
            } else {
                throw e;
            }
        } catch (GoodDataException e) {
            throw new GoodDataException("Unable to remove account", e);
        }
    }

    /**
     * Get account for given account id
     * @param id to search for
     * @return account for id
     * @throws AccountNotFoundException when account for given id can't be found
     * @throws GoodDataException when different error occurs
     */
    public Account getAccountById(final String id) {
        notNull(id, "id");
        try {
            return restTemplate.getForObject(Account.URI, Account.class, id);
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new AccountNotFoundException(Account.TEMPLATE.expand(id).toString(), e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get account", e);
        }
    }

    /**
     * Get account for given account id
     * @param uri to search for
     * @return account for uri
     * @throws AccountNotFoundException when account for given uri can't be found
     * @throws GoodDataException when different error occurs
     */
    public Account getAccountByUri(final String uri) {
        notEmpty(uri, "uri");
        return getAccountById(Account.getId(uri));
    }

    /**
     * Updates account
     * @param account to be updated
     * @throws AccountNotFoundException when account for given uri can't be found
     */
    public void updateAccount(final Account account) {
        notNull(account, "account");
        notNull(account.getUri(), "account.uri");

        try {
            final MappingJacksonValue jacksonValue = new MappingJacksonValue(account);
            jacksonValue.setSerializationView(Account.UpdateView.class);
            restTemplate.put(account.getUri(), jacksonValue);
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new AccountNotFoundException(account.getUri(), e);
            } else {
                throw e;
            }
        } catch (GoodDataException e) {
            throw new GoodDataException("Unable to update account", e);
        }
    }

}
