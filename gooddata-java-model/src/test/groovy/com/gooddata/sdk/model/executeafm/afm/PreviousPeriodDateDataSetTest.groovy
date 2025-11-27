/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm

import com.gooddata.sdk.model.executeafm.UriObjQualifier
import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class PreviousPeriodDateDataSetTest extends Specification {

    private static final String DATA_SET_JSON = "executeafm/afm/previousPeriodDateDataSet.json"

    def "should serialize"() {
        expect:
        def dateDataSet = new PreviousPeriodDateDataSet(
                new UriObjQualifier('/gdc/md/projectId/obj/1'), 2)
        that dateDataSet, jsonEquals(resource(DATA_SET_JSON))
    }

    def "should deserialize"() {
        when:
        def dateDataSet = readObjectFromResource("/$DATA_SET_JSON", PreviousPeriodDateDataSet)

        then:
        dateDataSet.dataSet.uri == '/gdc/md/projectId/obj/1'
        dateDataSet.periodsAgo == 2
    }

    def "test serializable"() {
        given:
        def dateDataSet = readObjectFromResource("/$DATA_SET_JSON", PreviousPeriodDateDataSet)
        def deserialized = SerializationUtils.roundtrip(dateDataSet)

        expect:
        that deserialized, jsonEquals(dateDataSet)
    }

    def "should throw when required construction parameter is null"() {
        when:
        new PreviousPeriodDateDataSet(dateDataSet, periodsAgo)

        then:
        thrown(IllegalArgumentException)

        where:
        dateDataSet                                    | periodsAgo
        null                                           | 2
        new UriObjQualifier('/gdc/md/projectId/obj/1') | null
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(PreviousPeriodDateDataSet)
                .usingGetClass()
                .verify()
    }
}
