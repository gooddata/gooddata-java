/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.lcm;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.collections.CustomPageRequest;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.PageRequest;
import com.gooddata.sdk.common.collections.PageBrowser;
import com.gooddata.sdk.common.util.SpringMutableUri;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.model.lcm.LcmEntities;
import com.gooddata.sdk.model.lcm.LcmEntity;
import com.gooddata.sdk.model.lcm.LcmEntityFilter;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.GoodDataSettings;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Service, which provides access to lifecycle management objects.
 */
public class LcmService extends AbstractService {

    public static final UriTemplate LCM_ENTITIES_TEMPLATE = new UriTemplate(LcmEntities.URI);

    /**
     * Constructs service for GoodData Life Cycle Management.
     * @param restTemplate RESTful HTTP Spring template
     * @param settings settings
     */
    public LcmService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        super(restTemplate, settings);
    }

    /**
     * Lists all {@link  LcmEntities} for given {@link Account}.
     * Returns empty list in case there is no {@link LcmEntity}.
     * Returns only first page if there's more instances than page limit. Use {@link PageBrowser#allItemsStream()} ()} to iterate
     * over all pages, or {@link PageBrowser#getAllItems()} ()} to load the entire list.
     *
     * @param account account to list LCM entities for
     * @return {@link PageBrowser} first page of list of lcm entities or empty list
     */
    public PageBrowser<LcmEntity> listLcmEntities(final Account account) {
        return listLcmEntities(account, new CustomPageRequest());
    }

    /**
     * Lists {@link  LcmEntities} for given {@link Account} filtered according given {@link LcmEntityFilter}.
     * Returns empty list in case there is no {@link LcmEntity}.
     * Returns only first page if there's more instances than page limit. Use {@link PageBrowser#allItemsStream()} ()} to iterate
     * over all pages, or {@link PageBrowser#getAllItems()} to load the entire list.
     *
     * @param account account to list LCM entities for
     * @param filter filter of the entities
     * @return {@link PageBrowser} first page of list of lcm entitiesor empty list
     */
    public PageBrowser<LcmEntity> listLcmEntities(final Account account, final LcmEntityFilter filter) {
        return listLcmEntities(account, filter, new CustomPageRequest());
    }

    /**
     * Lists all {@link  LcmEntities} for given {@link Account}.
     * Returns empty list in case there is no {@link LcmEntity}.
     * Returns requested page (by page limit and offset). Use {@link #listLcmEntities(Account)} to get first page with default setting.
     *
     * @param account account to list LCM entities for
     * @param startPage page to be listed
     * @return {@link PageBrowser} requested page of list of lcm entities or empty list
     */
    public PageBrowser<LcmEntity> listLcmEntities(final Account account, final PageRequest startPage) {
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
     * @return {@link PageBrowser} requested page of list of lcm entities or empty list
     */
    public PageBrowser<LcmEntity> listLcmEntities(final Account account, final LcmEntityFilter filter, final PageRequest startPage) {
        notNull(filter, "filter");
        notNull(startPage, "startPage");
        final String accountId = notNull(account, "account").getId();
        return new PageBrowser<>(startPage, page -> listLcmEntities(getLcmEntitiesUri(accountId, filter, page)));
    }

    private URI getLcmEntitiesUri(final String accountId) {
        return LCM_ENTITIES_TEMPLATE.expand(accountId);
    }

    private URI getLcmEntitiesUri(final String accountId, final LcmEntityFilter filter, final PageRequest page) {
        final SpringMutableUri mutableUri = new SpringMutableUri(getLcmEntitiesUri(accountId));
        mutableUri.replaceQueryParams(new LinkedMultiValueMap<>(filter.asQueryParams()));
        return page.getPageUri(mutableUri);
    }

    private Page<LcmEntity> listLcmEntities(final URI uri) {
        try {
            final LcmEntities result = restTemplate.getForObject(uri, LcmEntities.class);
            if (result == null) {
                return new Page<>();
            }
            return result;
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list LcmEntity", e);
        }
    }
}
