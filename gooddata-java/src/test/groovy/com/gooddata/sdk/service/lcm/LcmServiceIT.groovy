/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.lcm


import com.gooddata.sdk.model.account.Account
import com.gooddata.sdk.model.lcm.LcmEntityFilter
import com.gooddata.sdk.service.GoodDataITBase
import spock.lang.Shared
import spock.lang.Unroll

import static com.gooddata.util.ResourceUtils.readFromResource
import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.jadler.Jadler.onRequest

class LcmServiceIT extends GoodDataITBase<LcmService> {

    @Shared Account account = readObjectFromResource('/account/account.json', Account)
    @Shared LcmEntityFilter filter = new LcmEntityFilter().withDataProduct('dp').withSegment('seg').withClient('c')

    @Unroll
    def "should list #type"() {
        given:
        onRequest()
                .havingMethodEqualTo('GET')
                .havingPathEqualTo('/gdc/account/profile/ID/lcmEntities')
                .havingQueryStringEqualTo(queryString)
                .respond()
                .withBody(readFromResource('/lcm/lcmEntities.json'))
                .withStatus(200)

        when:
        def entities = list(service)

        then:
        entities.size() == 1

        where:
        type       | list                                    || queryString
        'all'      | { it.listLcmEntities(account) }         || 'limit=100'
        'filtered' | { it.listLcmEntities(account, filter) } || 'dataProduct=dp&segment=seg&client=c&limit=100'
    }



    @Override
    protected LcmService getService() {
        return gd.lcmService
    }
}
