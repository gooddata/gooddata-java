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

class PositiveAttributeFilterTest extends Specification {

    private static final String POSITIVE_ATTRIBUTE_FILTER_SIMPLE_JSON = 'executeafm/afm/positiveAttributeFilter.json'
    private static final String POSITIVE_ATTRIBUTE_FILTER_URIS_JSON = 'executeafm/afm/positiveAttributeFilterUris.json'
    private static final String POSITIVE_ATTRIBUTE_FILTER_VALUES_JSON = 'executeafm/afm/positiveAttributeFilterValues.json'

    private static final ObjQualifier QUALIFIER = new IdentifierObjQualifier('df.bum.bac')

    def "should serialize"() {
        expect:
        that new PositiveAttributeFilter(QUALIFIER, elements),
                jsonEquals(resource(jsonPath))

        where:
        elements                                     | jsonPath
        ['a', 'b']                                   | POSITIVE_ATTRIBUTE_FILTER_SIMPLE_JSON
        new UriAttributeFilterElements(['a', 'b'])   | POSITIVE_ATTRIBUTE_FILTER_URIS_JSON
        new ValueAttributeFilterElements(['a', 'b']) | POSITIVE_ATTRIBUTE_FILTER_VALUES_JSON
    }

    def "should deserialize"() {
        when:
        PositiveAttributeFilter filter = readObjectFromResource("/$jsonPath", PositiveAttributeFilter)

        then:
        with(filter) {
            displayForm == QUALIFIER
                    in.class == clazz
                    in.elements == ['a', 'b']
        }
        filter.toString()

        where:
        jsonPath                              | clazz
        POSITIVE_ATTRIBUTE_FILTER_SIMPLE_JSON | UriAttributeFilterElements
        POSITIVE_ATTRIBUTE_FILTER_URIS_JSON   | UriAttributeFilterElements
        POSITIVE_ATTRIBUTE_FILTER_VALUES_JSON | ValueAttributeFilterElements
    }

    def "should copy"() {
        when:
        def filter = new PositiveAttributeFilter(new IdentifierObjQualifier("id"))
        def copy = filter.withObjUriQualifier(new UriObjQualifier("uri"))

        then:
        copy.getObjQualifier().getUri() == "uri"
    }

    def "test serializable"() {
        PositiveAttributeFilter attributeFilter = readObjectFromResource("/$jsonPath", PositiveAttributeFilter)
        PositiveAttributeFilter deserialized = SerializationUtils.roundtrip(attributeFilter)

        expect:
        that deserialized, jsonEquals(attributeFilter)

        where:
        jsonPath << [
                POSITIVE_ATTRIBUTE_FILTER_SIMPLE_JSON,
                POSITIVE_ATTRIBUTE_FILTER_URIS_JSON,
                POSITIVE_ATTRIBUTE_FILTER_VALUES_JSON
        ]
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(PositiveAttributeFilter).usingGetClass().verify()
    }
}
