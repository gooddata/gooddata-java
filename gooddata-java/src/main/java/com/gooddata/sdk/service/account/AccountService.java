/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.account;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.model.account.Accounts;
import com.gooddata.sdk.model.account.SeparatorSettings;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.GoodDataSettings;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;
import static com.gooddata.sdk.common.util.Validate.notNullState;


/**
 * Service to access and manipulate account.
 */
public class AccountService extends AbstractService {


    public static final UriTemplate ACCOUNT_TEMPLATE = new UriTemplate(Account.URI);
    public static final UriTemplate ACCOUNTS_TEMPLATE = new UriTemplate(Account.ACCOUNTS_URI);
    public static final UriTemplate ACCOUNT_BY_LOGIN_TEMPLATE = new UriTemplate(Account.ACCOUNT_BY_EMAIL_URI);
    public static final UriTemplate LOGIN_TEMPLATE = new UriTemplate(Account.LOGIN_URI);
    public static final UriTemplate SEPARATORS_TEMPLATE = new UriTemplate(SeparatorSettings.URI);


    public AccountService(WebClient webClient, GoodDataSettings settings) {     
        super(webClient, settings); 
    }

    /**
     * Gets current account of logged user.
     *
     * @return current account
     * @throws com.gooddata.sdk.common.GoodDataException when current account can't be accessed.
     */
    public Account getCurrent() {
        return getAccountById(Account.CURRENT_ID);
    }

    /**
     * Performs user logout.
     *
     * @throws com.gooddata.sdk.common.GoodDataException when logout failed.
     */
    public void logout() {
        try {
            final String id = getCurrent().getId();
            webClient.delete()  
                    .uri(Account.LOGIN_URI, id)  
                    .retrieve()  
                    .toBodilessEntity() 
                    .block();  
        } catch (Exception e) {
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
            UriResponse uriResponse = webClient.post()  
                    .uri(uriBuilder -> uriBuilder
                            .path(Account.ACCOUNTS_URI)
                            .build(organizationName))   
                    .bodyValue(account)
                    .retrieve() 
                    .bodyToMono(UriResponse.class)
                    .block(); 
            return getAccountByUri(notNullState(uriResponse, "created account response").getUri());
        } catch (Exception e) {
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
            webClient.delete()  
                    .uri(account.getUri())  
                    .retrieve()     
                    .toBodilessEntity()     
                    .block();   
        } catch (Exception e) {
            throw new GoodDataException("Unable to remove account", e);
        }
    }

    public Account getAccountById(final String id) {
        notNull(id, "id");
        try {
            return webClient.get()  
                    .uri(uriBuilder -> uriBuilder
                            .path(Account.URI)
                            .build(id))     
                    .retrieve()     
                    .bodyToMono(Account.class)  
                    .block();   
        } catch (Exception e) {
            throw new GoodDataException("Unable to get account", e);
        }
    }


    /**
     * Get account by given login.
     * Only domain admin is allowed to search users by login.
     * @param email used as login
     * @param organizationName (domain) in which account is present
     * @return account found by given login
     * @throws AccountNotFoundException when given account wasn't found
     * @throws GoodDataException when different error occurs
     */
    public Account getAccountByLogin(final String email, final String organizationName) {
        notNull(email, "email");
        notNull(organizationName, "organizationName");
        try {
            Accounts accounts = webClient.get()     
                    .uri(uriBuilder -> uriBuilder
                            .path(Account.ACCOUNT_BY_EMAIL_URI)
                            .build(organizationName, email))    
                    .retrieve() 
                    .bodyToMono(Accounts.class)     
                    .block();   
            if (accounts != null && !accounts.getPageItems().isEmpty()) {
                return accounts.getPageItems().get(0);
            }
            throw new AccountNotFoundException("User was not found by email " +
                    email + " in organization " + organizationName, Account.ACCOUNT_BY_EMAIL_URI);
        } catch (Exception e) {
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
            // Changed: .put() with WebClient; manual JSON view can be used if needed
            webClient.put()
                    .uri(account.getUri())
                    .bodyValue(account)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new GoodDataException("Unable to update account", e);
        }
    }

    /**
     * Returns default thousand and decimal separator settings for the given account.
     *
     * @param account account
     * @return default {@link SeparatorSettings} for the account
     */
    public SeparatorSettings getSeparatorSettings(final Account account) {
        notNull(account, "account");
        notEmpty(account.getUri(), "account.uri");

        try {
            return webClient.get()  
                    .uri(SEPARATORS_TEMPLATE.expand(account.getId()))   
                    .retrieve()     
                    .bodyToMono(SeparatorSettings.class)    
                    .block();   
        } catch (Exception e) {
            throw new GoodDataException("Unable to get separators for account=" + account.getUri(), e);
        }
    }
}
