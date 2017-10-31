/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm

import com.gooddata.executeafm.IdentifierObjQualifier
import com.gooddata.executeafm.UriObjQualifier
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class PopMeasureDefinitionTest extends Specification {

    private static final String POP_MEASURE_DEFINITION_JSON = 'executeafm/afm/popMeasureDefinition.json'

    def "should serialize"() {
        expect:
        that new PopMeasureDefinition('mId', new UriObjQualifier('/gdc/md/projectId/obj/1')),
                jsonEquals(resource(POP_MEASURE_DEFINITION_JSON))
    }

    def "should deserialize"() {
        when:
        PopMeasureDefinition measure = readObjectFromResource("/$POP_MEASURE_DEFINITION_JSON", PopMeasureDefinition)

        then:
        measure.measureIdentifier == 'mId'
        (measure.popAttribute  as UriObjQualifier).uri == '/gdc/md/projectId/obj/1'
        measure.isAdHoc()
        measure.toString()
    }

    def "should copy"() {
        when:
        def measure = new PopMeasureDefinition("mid", new IdentifierObjQualifier("id"))
        def copy = measure.withObjUriQualifier(new UriObjQualifier("uri"))

        then:
        copy.objQualifier.uri == 'uri'
    }
}
