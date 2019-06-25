/*
 * Copyright (C) 2007-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service

import com.gooddata.sdk.service.httpcomponents.LoginPasswordGoodDataRestProvider
import spock.lang.Specification

class GoodDataServicesTest extends Specification {

    def "should init DataStoreService"() {
        expect:
        new GoodDataServices(new LoginPasswordGoodDataRestProvider(
                new GoodDataEndpoint(), new GoodDataSettings(),"sdk@gooddata.com","sdk")).dataStoreService
    }

    def "should not init DataStoreService using custom provider"() {
        expect:
        new GoodDataServices(Stub(GoodDataRestProvider)).dataStoreService == null
    }
}
