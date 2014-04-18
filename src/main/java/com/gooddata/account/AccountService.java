/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.account;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 */
public class AccountService extends AbstractService {

    public AccountService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Account getCurrent() {
        try {
            return restTemplate.getForObject(Account.URI, Account.class, Account.CURRENT_ID);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to get current account", e);
        }
    }

    public void logout() {
        try {
            final String id = getCurrent().getId();
            restTemplate.delete(Account.LOGIN_URI, id);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to logout", e);
        }
    }
}
