/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter

import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier
import com.gooddata.sdk.model.executeafm.UriObjQualifier
import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification

import java.time.LocalDate

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static java.time.LocalDate.now
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class AbsoluteDateFilterTest extends Specification {

    private static final String ABSOLUTE_DATE_FILTER_JSON = 'executeafm/afm/absoluteDateFilter.json'
    private static final String ABSOLUTE_DATE_FILTER_NO_ZERO_PAD_DATE_JSON = 'executeafm/afm/absoluteDateFilter_noZeroPadDate.json'

    private static final LocalDate FROM = LocalDate.of(2017, 9, 2)
    private static final LocalDate TO = LocalDate.of(2017, 9, 30)

    def "should serialize"() {
        expect:
        that new AbsoluteDateFilter(new IdentifierObjQualifier('date.attr'), FROM, TO),
                jsonEquals(resource(ABSOLUTE_DATE_FILTER_JSON))
    }

    def "should deserialize"() {
        when:
        AbsoluteDateFilter filter = readObjectFromResource("/$filterJson", AbsoluteDateFilter)

        then:
        with(filter) {
            (dataSet as IdentifierObjQualifier).identifier == 'date.attr'
            from == FROM
            to == TO
        }
        filter.toString()

        where:
        filterJson << [ABSOLUTE_DATE_FILTER_JSON, ABSOLUTE_DATE_FILTER_NO_ZERO_PAD_DATE_JSON]
    }


    def "should copy"() {
        when:
        def filter = new AbsoluteDateFilter(new IdentifierObjQualifier("id"), now(), now())
        def copy = filter.withObjUriQualifier(new UriObjQualifier("uri"))

        then:
        copy.getObjQualifier().getUri() == "uri"
    }

    def "test serializable"() {
        AbsoluteDateFilter dateFilter = readObjectFromResource("/$filterJson", AbsoluteDateFilter)
        AbsoluteDateFilter deserialized = SerializationUtils.roundtrip(dateFilter)

        expect:
        that deserialized, jsonEquals(dateFilter)

        where:
        filterJson << [ABSOLUTE_DATE_FILTER_JSON, ABSOLUTE_DATE_FILTER_NO_ZERO_PAD_DATE_JSON]
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(AbsoluteDateFilter).usingGetClass().verify()
    }
}
