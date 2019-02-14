/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.lcm;

import com.gooddata.sdk.model.lcm.LcmEntityFilter;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.collections.MultiPageList;
import com.gooddata.collections.Page;
import com.gooddata.collections.PageRequest;
import com.gooddata.collections.PageableList;
import com.gooddata.sdk.model.lcm.LcmEntities;
import com.gooddata.sdk.model.lcm.LcmEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.gooddata.util.Validate.notNull;

/**
 * Service, which provides access to lifecycle management objects.
 */
public class LcmService extends AbstractService {

    /**
     * Constructs service for GoodData Life Cycle Management.
     *
     * @param restTemplate RESTful HTTP Spring template
     * @param settings settings
     */
    public LcmService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        super(restTemplate, settings);
    }

    /**
     * Lists all {@link  LcmEntities} for given {@link Account}.
     * Returns empty list in case there is no {@link LcmEntity}.
     * Returns only first page if there's more instances than page limit. Use {@link PageableList#stream()} to iterate
     * over all pages, or {@link MultiPageList#collectAll()} to load the entire list.
     *
     * @param account account to list LCM entities for
     * @return MultiPageList first page of list of lcm entities or empty list
     */
    public PageableList<LcmEntity> listLcmEntities(final Account account) {
        return listLcmEntities(account, new PageRequest());
    }

    /**
     * Lists {@link  LcmEntities} for given {@link Account} filtered according given {@link LcmEntityFilter}.
     * Returns empty list in case there is no {@link LcmEntity}.
     * Returns only first page if there's more instances than page limit. Use {@link PageableList#stream()} to iterate
     * over all pages, or {@link MultiPageList#collectAll()} to load the entire list.
     *
     * @param account account to list LCM entities for
     * @param filter filter of the entities
     * @return MultiPageList first page of list of lcm entitiesor empty list
     */
    public PageableList<LcmEntity> listLcmEntities(final Account account, final LcmEntityFilter filter) {
        return listLcmEntities(account, filter, new PageRequest());
    }

    /**
     * Lists all {@link  LcmEntities} for given {@link Account}.
     * Returns empty list in case there is no {@link LcmEntity}.
     * Returns requested page (by page limit and offset). Use {@link #listLcmEntities(Account)} to get first page with default setting.
     *
     * @param account account to list LCM entities for
     * @param startPage page to be listed
     * @return MultiPageList requested page of list of lcm entities or empty list
     */
    public PageableList<LcmEntity> listLcmEntities(final Account account, final Page startPage) {
        return listLcmEntities(account, new LcmEntityFilter(), startPage);
    }

    /**
     * Lists {@link  LcmEntities} for given {@link Account} filtered according given {@link LcmEntityFilter}.
     * Returns empty list in case there is no {@link LcmEntity}.
     * Returns requested page (by page limit and offset). Use {@link #listLcmEntities(Account)} to get first page with default setting.
     *
     * @param account account to list LCM entities for
     * @param filter filter of the entities
     * @param startPage page to be listed
     * @return MultiPageList requested page of list of lcm entities or empty list
     */
    public PageableList<LcmEntity> listLcmEntities(final Account account, final LcmEntityFilter filter, final Page startPage) {
        notNull(filter, "filter");
        notNull(startPage, "startPage");
        final String accountId = notNull(account, "account").getId();
        return new MultiPageList<>(startPage, page -> listLcmEntities(getLcmEntitiesUri(accountId, filter, page)));
    }

    private URI getLcmEntitiesUri(final String accountId) {
        return LcmEntities.TEMPLATE.expand(accountId);
    }

    private URI getLcmEntitiesUri(final String accountId, final LcmEntityFilter filter, final Page page) {
        return page.getPageUri(
                UriComponentsBuilder.fromUri(getLcmEntitiesUri(accountId))
                        .queryParams(filter.asQueryParams()));
    }

    private PageableList<LcmEntity> listLcmEntities(final URI uri) {
        try {
            final LcmEntities result = restTemplate.getForObject(uri, LcmEntities.class);
            if (result == null) {
                return new PageableList<>();
            }
            return result;
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list LcmEntity", e);
        }
    }
}
