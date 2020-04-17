/*
 * Copyright (C) 2007-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.util

import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.sdk.model.util.UriHelper.getLastUriPart

class UriHelperTest extends Specification {

    @Unroll
    def "getLastUriPart(#uri) should return #result"() {
        expect:
        getLastUriPart(uri) == result

        where:
        uri               || result
        null              || null
        ''                || ''
        '/gdc/md/obj/123' || '123'
        '123'             || '123'
    }
}
