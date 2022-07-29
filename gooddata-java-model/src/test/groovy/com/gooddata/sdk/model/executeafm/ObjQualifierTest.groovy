/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm

import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource

class ObjQualifierTest extends Specification {

    @Unroll
    def "should deserialize as #type"() {
        when:
        ObjQualifier qualifier = readObjectFromResource("/executeafm/${type}.json", ObjQualifier)

        then:
        typeClass.isInstance(qualifier)

        where:
        type                     | typeClass
        'identifierObjQualifier' | IdentifierObjQualifier
        'uriObjQualifier'        | UriObjQualifier
    }

    def "getUri() should throw exception"() {
        when:
        def qualifier = new ObjQualifier() {}
        qualifier.getUri()
        then:
        def exception = thrown(UnsupportedOperationException)
        exception.message == "This qualifier has no URI"
    }
}
