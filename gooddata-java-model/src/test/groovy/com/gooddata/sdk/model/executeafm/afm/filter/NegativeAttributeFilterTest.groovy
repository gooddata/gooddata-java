/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter

import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier
import com.gooddata.sdk.model.executeafm.ObjQualifier
import com.gooddata.sdk.model.executeafm.UriObjQualifier
import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class NegativeAttributeFilterTest extends Specification {

    private static final String NEGATIVE_ATTRIBUTE_FILTER_SIMPLE_JSON = 'executeafm/afm/negativeAttributeFilter.json'
    private static final String NEGATIVE_ATTRIBUTE_FILTER_URIS_JSON = 'executeafm/afm/negativeAttributeFilterUris.json'
    private static final String NEGATIVE_ATTRIBUTE_FILTER_VALUES_JSON = 'executeafm/afm/negativeAttributeFilterValues.json'

    private static final ObjQualifier QUALIFIER = new IdentifierObjQualifier('df.bum.bac')

    def "should serialize"() {
        expect:
        that new NegativeAttributeFilter(QUALIFIER, elements),
                jsonEquals(resource(jsonPath))

        where:
        elements                                     | jsonPath
        ['a', 'b']                                   | NEGATIVE_ATTRIBUTE_FILTER_SIMPLE_JSON
        new UriAttributeFilterElements(['a', 'b'])   | NEGATIVE_ATTRIBUTE_FILTER_URIS_JSON
        new ValueAttributeFilterElements(['a', 'b']) | NEGATIVE_ATTRIBUTE_FILTER_VALUES_JSON
    }

    def "should deserialize"() {
        when:
        NegativeAttributeFilter filter = readObjectFromResource("/$jsonPath", NegativeAttributeFilter)

        then:
        with(filter) {
            displayForm == QUALIFIER
            notIn.class == clazz
            notIn.elements == ['a', 'b']
        }
        filter.toString()

        where:
        jsonPath                              | clazz
        NEGATIVE_ATTRIBUTE_FILTER_SIMPLE_JSON | UriAttributeFilterElements
        NEGATIVE_ATTRIBUTE_FILTER_URIS_JSON   | UriAttributeFilterElements
        NEGATIVE_ATTRIBUTE_FILTER_VALUES_JSON | ValueAttributeFilterElements
    }

    def "should copy"() {
        when:
        def filter = new NegativeAttributeFilter(new IdentifierObjQualifier("id"))
        def copy = filter.withObjUriQualifier(new UriObjQualifier("uri"))

        then:
        copy.getObjQualifier().getUri() == "uri"
    }

    def "test serializable"() {
        NegativeAttributeFilter attributeFilter = readObjectFromResource("/$jsonPath", NegativeAttributeFilter)
        NegativeAttributeFilter deserialized = SerializationUtils.roundtrip(attributeFilter)

        expect:
        that deserialized, jsonEquals(attributeFilter)

        where:
        jsonPath << [
                NEGATIVE_ATTRIBUTE_FILTER_SIMPLE_JSON,
                NEGATIVE_ATTRIBUTE_FILTER_URIS_JSON,
                NEGATIVE_ATTRIBUTE_FILTER_VALUES_JSON
        ]
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(NegativeAttributeFilter).usingGetClass().verify()
    }
}

