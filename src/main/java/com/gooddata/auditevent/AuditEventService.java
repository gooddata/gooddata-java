/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.auditevent;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import com.gooddata.collections.MultiPageList;
import com.gooddata.collections.Page;
import com.gooddata.collections.PageableList;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * List audit events.
 */
public class AuditEventService extends AbstractService {

    private final AccountService accountService;

    /**
     * Service for audit events
     * @param restTemplate rest template
     * @param accountService account service
     */
    public AuditEventService(final RestTemplate restTemplate, final AccountService accountService) {
        super(restTemplate);
        this.accountService = notNull(accountService, "account service");
    }

    /**
     * Get list of audit events for the given domain id
     * @param domainId domain id
     * @return non-null paged list of events
     * @throws AuditEventsForbiddenException if current user is not admin of the given domain
     */
    public PageableList<AuditEvent> listAuditEvents(final String domainId) {
        return listAuditEvents(domainId, new AuditEventPageRequest());
    }

    /**
     * Get list of audit events for the given domain id
     * @param domainId domain id
     * @param page request parameters
     * @return non-null paged list of events
     * @throws AuditEventsForbiddenException if current user is not admin of the given domain
     */
    public PageableList<AuditEvent> listAuditEvents(final String domainId, final Page page) {
        notEmpty(domainId, "domainId");
        notNull(page, "page");

        final String uri = AuditEvent.ADMIN_URI_TEMPLATE.expand(domainId).toString();
        return new MultiPageList<>(page, (p) -> doListAuditEvents(getAuditEventsUri(p, uri)));
    }

    /**
     * Get list of audit events for the given account
     * @param account account with valid id
     * @return non-null paged list of events
     * @throws AuditEventsForbiddenException if audit events are not enabled for the given user or the current user is
     * not domain admin
     */
    public PageableList<AuditEvent> listAuditEvents(final Account account) {
        return listAuditEvents(account, new AuditEventPageRequest());
    }

    /**
     * Get list of audit events for the given account
     * @param account account with valid id
     * @param page request parameters
     * @return non-null paged list of events
     * @throws AuditEventsForbiddenException if audit events are not enabled for the given user or the current user is
     * not domain admin
     */
    public PageableList<AuditEvent> listAuditEvents(final Account account, final Page page) {
        notNull(account, "account");
        notEmpty(account.getId(), "account.id");
        notNull(page, "page");

        final String uri = AuditEvent.USER_URI_TEMPLATE.expand(account.getId()).toString();

        return new MultiPageList<>(page, (p) -> doListAuditEvents(getAuditEventsUri(p, uri)));
    }

    /**
     * Get list of audit events for current account
     * @return non-null paged list of events
     * @throws AuditEventsForbiddenException if audit events are not enabled for current user
     */
    public PageableList<AuditEvent> listAuditEvents() {
        return listAuditEvents(new AuditEventPageRequest());
    }

    /**
     * Get list of audit events for current account
     * @param page request parameters
     * @return non-null paged list of events
     * @throws AuditEventsForbiddenException if audit events are not enabled for current user
     */
    public PageableList<AuditEvent> listAuditEvents(final Page page) {
        notNull(page, "page");
        final Account account = accountService.getCurrent();

        return listAuditEvents(account, page);
    }

    private AuditEvents doListAuditEvents(final String uri) {
        try {
            return restTemplate.getForObject(uri, AuditEvents.class);
        } catch (GoodDataRestException e) {
            if (UNAUTHORIZED.value() == e.getStatusCode()) {
                throw new AuditEventsForbiddenException(e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to list audit events: " + uri);
        }
    }

    private String getAuditEventsUri(final Page page, final String uri) {
        return page.updateWithPageParams(UriComponentsBuilder.fromUriString(uri)).build().toUriString();
    }
}

