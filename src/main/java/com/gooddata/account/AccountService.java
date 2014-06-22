/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.account;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service to access and manipulate account.
 */
public class AccountService extends AbstractService {

    public AccountService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * Gets current account of logged user.
     * @throws com.gooddata.GoodDataException when current account can't be accessed.
     *
     * @return current account
     */
    public Account getCurrent() {
        try {
            return restTemplate.getForObject(Account.URI, Account.class, Account.CURRENT_ID);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to get current account", e);
        }
    }

    /**
     * Performs user logout.
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
}
