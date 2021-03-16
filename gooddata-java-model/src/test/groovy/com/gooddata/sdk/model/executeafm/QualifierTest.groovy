/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm

import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource

class QualifierTest extends Specification {

    private static final String IDENTIFIER_OBJ_QUALIFIER_JSON = '/executeafm/identifierObjQualifier.json'
    private static final String URI_OBJ_QUALIFIER_JSON = '/executeafm/uriObjQualifier.json'
    private static final String LOCAL_IDENTIFIER_QUALIFIER_JSON = '/executeafm/localIdentifierQualifier.json'

    @Unroll
    def "should deserialize as #type"() {
        when:
        Qualifier qualifier = readObjectFromResource(jsonPath, Qualifier)

        then:
        typeClass.isInstance(qualifier)

        where:
        typeClass                | jsonPath
        IdentifierObjQualifier   | IDENTIFIER_OBJ_QUALIFIER_JSON
        UriObjQualifier          | URI_OBJ_QUALIFIER_JSON
        LocalIdentifierQualifier | LOCAL_IDENTIFIER_QUALIFIER_JSON

        type = typeClass.simpleName
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
