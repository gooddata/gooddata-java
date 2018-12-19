/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.visualization

import com.gooddata.sdk.model.executeafm.UriObjQualifier
import com.gooddata.sdk.model.executeafm.afm.AbsoluteDateFilter
import com.gooddata.sdk.model.executeafm.afm.NegativeAttributeFilter
import com.gooddata.sdk.model.executeafm.afm.PositiveAttributeFilter
import com.gooddata.sdk.model.executeafm.afm.RelativeDateFilter
import org.apache.commons.lang3.SerializationUtils
import org.joda.time.LocalDate
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
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

    def "should deserialize full"() {
        expect:
        complexVisualization?.title == 'complex'
        complexVisualization?.visualizationClassUri == 'visClass'
        complexVisualization?.buckets?.size() == 2
        complexVisualization?.buckets?.find { it.localIdentifier == 'bucket1' }?.items?.every { it.class == VisualizationAttribute }
        complexVisualization?.buckets?.find { it.localIdentifier == 'bucket2' }?.items?.every { it.class == Measure }
        complexVisualization?.filters?.size() == 4
        complexVisualization?.filters?.find { it.class == PositiveAttributeFilter }
        complexVisualization?.filters?.find { it.class == NegativeAttributeFilter }
        complexVisualization?.filters?.find { it.class == AbsoluteDateFilter }
        complexVisualization?.filters?.find { it.class == RelativeDateFilter }
        complexVisualization?.properties == '{"key":"value"}'
        complexVisualization?.referenceItems?.size() == 2
    }

    def "should serialize full"() {
        given:
        VOSimpleMeasureDefinition measureDefinition = new VOSimpleMeasureDefinition(new UriObjQualifier("/uri/to/measure/1"), 'sum', false,
                [new PositiveAttributeFilter(new UriObjQualifier("/uri/to/displayForm/3")),
                 new NegativeAttributeFilter(new UriObjQualifier("/uri/to/displayForm/3"), ["ab", "cd"]),
                 new AbsoluteDateFilter(new UriObjQualifier("/uri/to/dataSet/1"), null, null),
                 new RelativeDateFilter(new UriObjQualifier("/uri/to/dataSet/2"), "month", 0, -11)])

        Map<String, String> references = new HashMap<>()
        references.put("key", "value")
        references.put("foo", "bar")

        VisualizationObject vizObject = new VisualizationObject('complex', 'visClass')
        vizObject.buckets = [
                new Bucket('bucket1', [new VisualizationAttribute(new UriObjQualifier("/uri/to/displayForm/1"), "attribute1", "attributeAlias")]),
                new Bucket('bucket2', [
                            new Measure(
                                    new VOSimpleMeasureDefinition(new UriObjQualifier('/uri/to/measure/0')),
                                    'measure0',
                                    'Measure 0 alias',
                                    null,
                                    null
                            ),
                            new Measure(
                                    measureDefinition,
                                    'measure1',
                                    'Measure 1 alias',
                                    'Measure 1',
                                    null
                            )
                    ])
            ]
        vizObject.filters = [
                new PositiveAttributeFilter(new UriObjQualifier("/uri/to/displayForm/3"), ["ab", "cd"]),
                new NegativeAttributeFilter(new UriObjQualifier("/uri/to/displayForm/3"), []),
                new AbsoluteDateFilter(new UriObjQualifier("/uri/to/dataSet/1"), new LocalDate("2000-08-30"), new LocalDate("2017-08-07")),
                new RelativeDateFilter(new UriObjQualifier("/uri/to/dataSet/2"), "month", null, null)
            ]
        vizObject.properties = '{"key":"value"}'
        vizObject.referenceItems = references

        expect:
        that vizObject, jsonEquals(resource(COMPLEX_VISUALIZATION))
    }

    def "should serialize simple"() {
        given:
        VisualizationObject simpleVisualization = readObjectFromResource("/$SIMPLE_VISUALIZATION", VisualizationObject)

        expect:
        that new VisualizationObject('simple', 'visClass'), jsonEquals(simpleVisualization)
    }

    def "should return null when invalid collection requested"() {
        given:
        VisualizationObject customChart = readObjectFromResource("/$CUSTOM_CHART", VisualizationObject)

        when:
        VisualizationAttribute fromMissingCollection = stackedColumnChart.getAttributeFromCollection(CollectionType.TREND)
        VisualizationAttribute missingAttribute = customChart.getAttributeFromCollection(CollectionType.VIEW)

        then:
        fromMissingCollection == null
        missingAttribute == null
    }

    @Unroll
    "return item in #name collection"() {
        expect:
        getter() == expected

        where:
        name      | getter                         | expected
        "view"    | stackedColumnChart.&getView    | stackedColumnChart.buckets[0].items[0]
        "stack"   | stackedColumnChart.&getStack   | stackedColumnChart.buckets[1].items[0]
        "trend"   | segmentedLineChart.&getTrend   | segmentedLineChart.buckets[0].items[0]
        "segment" | segmentedLineChart.&getSegment | segmentedLineChart.buckets[1].items[0]
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
        multipleMeasuresVisualization.getMeasures() | [ multipleMeasuresVisualization.buckets[0].items[0],
                                                        multipleMeasuresVisualization.buckets[1].items[0],
                                                        multipleMeasuresVisualization.buckets[1].items[1] ]
        emptyBucketsVisualization.getMeasures()       | []
    }

    def "return only simple measures"() {
        expect:
        measures == expected

        where:
        measures                                          | expected
        multipleMeasuresVisualization.getSimpleMeasures() | [ multipleMeasuresVisualization.buckets[0].items[0],
                                                              multipleMeasuresVisualization.buckets[1].items[0] ]
        emptyBucketsVisualization.getSimpleMeasures()     | []
    }

    def "should return attributes"() {
        expect:
        attributes == expected

        where:
        attributes                                | expected
        segmentedLineChart.getAttributes()        | [ segmentedLineChart.buckets[0].items[0],
                                                      segmentedLineChart.buckets[1].items[0] ]
        emptyBucketsVisualization.getAttributes() | []
    }

    def "should check if visualization object has derived measures"() {
        given:
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
    "should set #property"() {
        given:
        VisualizationObject empty = readObjectFromResource("/$EMPTY_BUCKETS", VisualizationObject)

        when:
        empty."set$property"(complexVisualization."get$property"())

        then:
        empty."get$property"() == complexVisualization."get$property"()

        where:
        property << ["VisualizationClass", "Buckets", "Filters", "Properties", "ReferenceItems"]
    }

    def "test serializable"() {
        when:
        VisualizationObject deserialized = SerializationUtils.roundtrip(complexVisualization)

        then:
        that deserialized, jsonEquals(complexVisualization)
    }
}