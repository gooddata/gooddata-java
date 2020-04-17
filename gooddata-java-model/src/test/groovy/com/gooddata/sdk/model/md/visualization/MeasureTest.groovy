/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.visualization

import com.gooddata.sdk.model.executeafm.UriObjQualifier
import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Shared
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class MeasureTest extends Specification {
    private static final String MULTIPLE_MEASURES_BUCKET = "md/visualization/multipleMeasuresBucket.json"
    private static final String MEASURE_BUCKET = "md/visualization/measureBucket.json"
    public static final String SIMPLE_MEASURE_JSON = 'md/visualization/simpleMeasure.json'

    @Shared
    Bucket bucket = readObjectFromResource("/$MULTIPLE_MEASURES_BUCKET", Bucket)
    @Shared
    Measure measureWithCr = bucket.getItems().get(0)
    @Shared
    Measure measureWithoutCr = bucket.getItems().get(1)
    @Shared
    Measure popMeasure = bucket.getItems().get(2)

    def "should check if is pop measure"() {
        expect:
        popMeasure.isPop()

        and:
        !measureWithCr.isPop()
        !measureWithoutCr.isPop()
    }

    def "should check if has compute ratio"() {
        expect:
        measureWithCr.hasComputeRatio()

        and:
        !measureWithoutCr.hasComputeRatio()
        !popMeasure.hasComputeRatio()
    }

    def "should set title"() {
        Bucket measureBucket = readObjectFromResource("/$MEASURE_BUCKET", Bucket)
        Measure measure = measureBucket.getItems().get(0)

        when:
        measure.setTitle("new title")

        then:
        measure.getTitle() == "new title"
    }

    def "test serializable"() {
        Measure deserialized = SerializationUtils.roundtrip(measureWithCr)

        expect:
        that deserialized, jsonEquals(measureWithCr)
    }

    @SuppressWarnings("GrDeprecatedAPIUsage")
    def "test serialize simple"() {
        expect:
        that new Measure(new VOSimpleMeasureDefinition(new UriObjQualifier('uri')), 'loc1'), jsonEquals(resource(SIMPLE_MEASURE_JSON))
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(Measure).usingGetClass()
                .withIgnoredFields("alias")
                .withIgnoredFields("title")
                .withIgnoredFields("format")
                .verify()
    }
}
