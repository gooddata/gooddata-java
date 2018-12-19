/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm

import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier
import com.gooddata.sdk.model.executeafm.UriObjQualifier
import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import org.joda.time.LocalDate
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static org.joda.time.LocalDate.now
import static spock.util.matcher.HamcrestSupport.that

class AbsoluteDateFilterTest extends Specification {

    private static final String ABSOLUTE_DATE_FILTER_JSON = 'executeafm/afm/absoluteDateFilter.json'

    private static final LocalDate FROM = new LocalDate(2017, 9, 25)
    private static final LocalDate TO = new LocalDate(2017, 9, 30)

    def "should serialize"() {
        expect:
        that new AbsoluteDateFilter(new IdentifierObjQualifier('date.attr'), FROM, TO),
                jsonEquals(resource(ABSOLUTE_DATE_FILTER_JSON))
    }

    def "should deserialize"() {
        when:
        AbsoluteDateFilter filter = readObjectFromResource("/$ABSOLUTE_DATE_FILTER_JSON", AbsoluteDateFilter)

        then:
        with(filter) {
            (dataSet as IdentifierObjQualifier).identifier == 'date.attr'
            from == FROM
            to == TO
        }
        filter.toString()
    }


    def "should copy"() {
        when:
        def filter = new AbsoluteDateFilter(new IdentifierObjQualifier("id"), now(), now())
        def copy = filter.withObjUriQualifier(new UriObjQualifier("uri"))

        then:
        copy.getObjQualifier().getUri() == "uri"
    }

    def "test serializable"() {
        AbsoluteDateFilter dateFilter = readObjectFromResource("/$ABSOLUTE_DATE_FILTER_JSON", AbsoluteDateFilter)
        AbsoluteDateFilter deserialized = SerializationUtils.roundtrip(dateFilter)

        expect:
        that deserialized, jsonEquals(dateFilter)
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(AbsoluteDateFilter).usingGetClass().verify()
    }
}
