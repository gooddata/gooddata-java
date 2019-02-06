/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm

import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier
import com.gooddata.sdk.model.executeafm.ObjQualifier
import com.gooddata.sdk.model.executeafm.UriObjQualifier
import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class NegativeAttributeFilterTest extends Specification {

    private static final String NEGATIVE_ATTRIBUTE_FILTER_JSON = 'executeafm/afm/negativeAttributeFilter.json'

    private static final ObjQualifier QUALIFIER = new IdentifierObjQualifier('df.bum.bac')

    def "should serialize"() {
        expect:
        that new NegativeAttributeFilter(QUALIFIER, 'a', 'b'),
                jsonEquals(resource(NEGATIVE_ATTRIBUTE_FILTER_JSON))
    }

    def "should deserialize"() {
        when:
        NegativeAttributeFilter filter = readObjectFromResource("/$NEGATIVE_ATTRIBUTE_FILTER_JSON", NegativeAttributeFilter)

        then:
        with(filter) {
            displayForm == QUALIFIER
            notIn == ['a', 'b']
        }
        filter.toString()
    }

    def "should copy"() {
        when:
        def filter = new NegativeAttributeFilter(new IdentifierObjQualifier("id"))
        def copy = filter.withObjUriQualifier(new UriObjQualifier("uri"))

        then:
        copy.getObjQualifier().getUri() == "uri"
    }

    def "test serializable"() {
        NegativeAttributeFilter attributeFilter = readObjectFromResource("/$NEGATIVE_ATTRIBUTE_FILTER_JSON", NegativeAttributeFilter)
        NegativeAttributeFilter deserialized = SerializationUtils.roundtrip(attributeFilter)

        expect:
        that deserialized, jsonEquals(attributeFilter)
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(NegativeAttributeFilter).usingGetClass().verify()
    }
}
