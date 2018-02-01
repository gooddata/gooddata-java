/*
 * Copyright (C) 2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.md.visualization

import com.fasterxml.jackson.databind.ObjectMapper
import com.gooddata.executeafm.UriObjQualifier
import com.gooddata.executeafm.afm.AbsoluteDateFilter
import com.gooddata.executeafm.afm.NegativeAttributeFilter
import com.gooddata.executeafm.afm.PositiveAttributeFilter
import com.gooddata.executeafm.afm.RelativeDateFilter
import com.gooddata.md.Meta
import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import org.joda.time.LocalDate
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static spock.util.matcher.HamcrestSupport.that

class VisualizationObjectTest extends Specification {
    private static final String COMPLEX_VISUALIZATION = "md/visualization/complexVisualizationObject.json"
    private static final String SIMPLE_VISUALIZATION = "md/visualization/simpleVisualizationObject.json"
    private static final String STACKED_COLUMN_CHART = "md/visualization/stackedColumnChart.json"
    private static final String SEGMENTED_LINE_CHART = "md/visualization/segmentedLineChart.json"
    private static final String CUSTOM_CHART = "md/visualization/customChart.json"
    private static final String EMPTY_BUCKETS = "md/visualization/emptyBucketsVisualization.json"
    private static final String MULTIPLE_MEASURE_BUCKETS = "md/visualization/multipleMeasureBucketsVisualization.json"

    @Shared
    VisualizationObject stackedColumnChart = readObjectFromResource("/$STACKED_COLUMN_CHART", VisualizationObject)
    @Shared
    VisualizationObject segmentedLineChart = readObjectFromResource("/$SEGMENTED_LINE_CHART", VisualizationObject)
    @Shared
    VisualizationObject emptyBucketsVisualization = readObjectFromResource("/$EMPTY_BUCKETS", VisualizationObject)
    @Shared
    VisualizationObject complexVisualization = readObjectFromResource("/$COMPLEX_VISUALIZATION", VisualizationObject)
    @Shared
    VisualizationObject multipleMeasuresVisualization = readObjectFromResource("/$MULTIPLE_MEASURE_BUCKETS", VisualizationObject)

    def "should serialize full"() {
        VisualizationAttribute attribute1 = new VisualizationAttribute(new UriObjQualifier("/uri/to/displayForm/1"), "attribute1", "attributeAlias")
        PositiveAttributeFilter positiveAttributeFilter = new PositiveAttributeFilter( new UriObjQualifier("/uri/to/displayForm/3"), ["ab", "cd"])
        NegativeAttributeFilter negativeAttributeFilter = new NegativeAttributeFilter( new UriObjQualifier("/uri/to/displayForm/3"), ["ab", "cd"])
        AbsoluteDateFilter absoluteDateFilter = new AbsoluteDateFilter( new UriObjQualifier("/uri/to/dataSet/1"), new LocalDate("2000-08-30"), new LocalDate("2017-08-07"))
        RelativeDateFilter relativeDateFilter = new RelativeDateFilter( new UriObjQualifier("/uri/to/dataSet/2"), "month", 0, -11)
        VOSimpleMeasureDefinition measureDefinition = new VOSimpleMeasureDefinition(new UriObjQualifier("/uri/to/measure/1"), "sum", false, [positiveAttributeFilter, negativeAttributeFilter, absoluteDateFilter, relativeDateFilter ])
        Map<String, String> references = new HashMap<>()
        references.put("key", "value")
        references.put("foo", "bar")

        expect:
        that new VisualizationObject(
                new VisualizationObject.Content(
                        new UriObjQualifier("visClass"),
                        [
                                new Bucket("bucket1", [attribute1]),
                                new Bucket("bucket2", [
                                        new Measure(
                                                measureDefinition,
                                                "measure1",
                                                "Measure 1 alias",
                                                "Measure 1",
                                                null
                                        )
                                ])
                        ],
                        [
                                positiveAttributeFilter,
                                negativeAttributeFilter,
                                absoluteDateFilter,
                                relativeDateFilter
                        ],
                        "{\"key\":\"value\"}",
                        references
                ),
                new Meta("complex")
        ), jsonEquals(complexVisualization)
    }

    def "should serialize"() {
        VisualizationObject simpleVisualization = readObjectFromResource("/$SIMPLE_VISUALIZATION", VisualizationObject)

        expect:
        that new VisualizationObject(
                new VisualizationObject.Content(
                        new UriObjQualifier("visClass"),
                        [],
                        null,
                        null,
                        null
                ),
                new Meta("simple")
        ), jsonEquals(simpleVisualization)
    }

    def "should return null when invalid collection requested"() {
        VisualizationObject customChart = readObjectFromResource("/$CUSTOM_CHART", VisualizationObject)

        when:
        VisualizationAttribute fromMissingCollection = stackedColumnChart.getAttributeFromCollection(CollectionType.TREND)
        VisualizationAttribute missingAttribute = customChart.getAttributeFromCollection(CollectionType.VIEW)

        then:
        fromMissingCollection == null
        missingAttribute == null
    }

    @Unroll
    def "return item in #name collection"() {
        VisualizationAttribute attribute = getter()

        expect:
        attribute == expected

        where:
        name      | getter                         | expected
        "view"    | stackedColumnChart.&getView    | stackedColumnChart.getBuckets().get(0).getItems().get(0)
        "stack"   | stackedColumnChart.&getStack   | stackedColumnChart.getBuckets().get(1).getItems().get(0)
        "trend"   | segmentedLineChart.&getTrend   | segmentedLineChart.getBuckets().get(0).getItems().get(0)
        "segment" | segmentedLineChart.&getSegment | segmentedLineChart.getBuckets().get(1).getItems().get(0)
    }

    def "should return null when non-existing key requested"() {
        expect:
        complexVisualization.getItemById("foo") == "bar"

        and:
        complexVisualization.getItemById("invalid") == null
    }

    def "should return measures"() {
        expect:
        measures == expected

        where:
        measures                                    | expected
        multipleMeasuresVisualization.getMeasures() | [ multipleMeasuresVisualization.content.buckets.get(0).getItems().get(0),
                                                        multipleMeasuresVisualization.content.buckets.get(1).getItems().get(0),
                                                        multipleMeasuresVisualization.content.buckets.get(1).getItems().get(1) ]
        emptyBucketsVisualization.getMeasures()       | []
    }

    def "return only simple measures"() {
        expect:
        measures == expected

        where:
        measures                                          | expected
        multipleMeasuresVisualization.getSimpleMeasures() | [ multipleMeasuresVisualization.content.buckets.get(0).getItems().get(0),
                                                              multipleMeasuresVisualization.content.buckets.get(1).getItems().get(0) ]
        emptyBucketsVisualization.getSimpleMeasures()     | []
    }

    def "should return attributes"() {
        expect:
        attributes == expected

        where:
        attributes                                | expected
        segmentedLineChart.getAttributes()        | [ segmentedLineChart.content.buckets.get(0).getItems().get(0),
                                                      segmentedLineChart.content.buckets.get(1).getItems().get(0) ]
        emptyBucketsVisualization.getAttributes() | []
    }

    def "should check if visualization object has derived measures"() {
        VisualizationObject visualizationWithPop = multipleMeasuresVisualization
        VisualizationObject visualizationWithoutPop = segmentedLineChart
        VisualizationObject noMeasuresVisualization = emptyBucketsVisualization

        expect:
        visualizationWithPop.hasDerivedMeasure()

        and:
        !visualizationWithoutPop.hasDerivedMeasure()
        !noMeasuresVisualization.hasDerivedMeasure()
    }

    @Unroll
    def "should set #property"() {
        VisualizationObject empty = readObjectFromResource("/$EMPTY_BUCKETS", VisualizationObject)

        when:
        empty."set$property"(complexVisualization."get$property"())

        then:
        empty."get$property"() == complexVisualization."get$property"()

        where:
        property << ["VisualizationClass", "Buckets", "Filters", "Properties", "ReferenceItems"]
    }

    def "test serializable"() {
        VisualizationObject deserialized = SerializationUtils.roundtrip(complexVisualization)

        expect:
        that deserialized, jsonEquals(complexVisualization)
    }
}