/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm

import com.gooddata.executeafm.IdentifierObjQualifier
import com.gooddata.executeafm.ObjQualifier
import com.gooddata.executeafm.UriObjQualifier
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class PositiveAttributeFilterTest extends Specification {

    private static final String POSITIVE_ATTRIBUTE_FILTER_JSON = 'executeafm/afm/positiveAttributeFilter.json'

    private static final ObjQualifier QUALIFIER = new IdentifierObjQualifier('df.bum.bac')

    def "should serialize"() {
        expect:
        that new PositiveAttributeFilter(QUALIFIER, 'a', 'b'),
                jsonEquals(resource(POSITIVE_ATTRIBUTE_FILTER_JSON))
    }

    def "should deserialize"() {
        when:
        PositiveAttributeFilter filter = readObjectFromResource("/$POSITIVE_ATTRIBUTE_FILTER_JSON", PositiveAttributeFilter)

        then:
        with(filter) {
            displayForm == QUALIFIER
            getIn() == ['a', 'b']
        }
        filter.toString()
    }

    def "should copy"() {
        when:
        def filter = new PositiveAttributeFilter(new IdentifierObjQualifier("id"))
        def copy = filter.withObjUriQualifier(new UriObjQualifier("uri"))

        then:
        copy.getObjQualifier().getUri() == "uri"
    }
}
