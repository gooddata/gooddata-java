/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.account;

import com.gooddata.AbstractService;
import org.springframework.web.client.RestTemplate;

/**
 */
public class AccountService extends AbstractService {

    public AccountService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Account getCurrent() {
        return restTemplate.getForObject(Account.URI, Account.class, Account.CURRENT_ID);
    }

    public void logout() {
        final String id = getCurrent().getId();
        restTemplate.delete(Account.LOGIN_URI, id);
    }
}
