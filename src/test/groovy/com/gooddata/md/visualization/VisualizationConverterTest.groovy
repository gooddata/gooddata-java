/*
 * Copyright (C) 2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.visualization

import com.fasterxml.jackson.core.JsonParseException
import com.gooddata.executeafm.UriObjQualifier
import org.joda.time.LocalDate
import spock.lang.Specification
import com.gooddata.executeafm.afm.AbsoluteDateFilter
import com.gooddata.executeafm.afm.Afm
import com.gooddata.executeafm.afm.AttributeItem
import com.gooddata.executeafm.afm.MeasureItem
import com.gooddata.executeafm.afm.NegativeAttributeFilter
import com.gooddata.executeafm.afm.PositiveAttributeFilter
import com.gooddata.executeafm.afm.RelativeDateFilter
import com.gooddata.executeafm.resultspec.AttributeSortItem
import com.gooddata.executeafm.resultspec.Dimension
import com.gooddata.executeafm.resultspec.MeasureLocatorItem
import com.gooddata.executeafm.resultspec.MeasureSortItem
import com.gooddata.executeafm.resultspec.ResultSpec
import com.gooddata.executeafm.resultspec.SortItem
import spock.lang.Unroll

import java.util.function.Function

import static VisualizationConverter.convertToAfm
import static VisualizationConverter.convertToResultSpec
import static VisualizationConverter.parseSorting
import static com.gooddata.md.visualization.VisualizationConverter.convertToExecution
import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static spock.util.matcher.HamcrestSupport.that

class VisualizationConverterTest extends Specification {
    private static final String COMPLEX_VISUALIZATION = "md/visualization/complexVisualizationObject.json"
    private static final String SIMPLE_VISUALIZATION = "md/visualization/simpleVisualizationObject.json"

    private static final String MULTIPLE_MEASURE_BUCKETS = "md/visualization/multipleMeasureBucketsVisualization.json"
    private static final String MULTIPLE_ATTRIBUTE_BUCKETS = "md/visualization/multipleAttributeBucketsVisualization.json"
    private static final String STACKED_COLUMN_CHART = "md/visualization/stackedColumnChart.json"
    private static final String LINE_CHART = "md/visualization/lineChart.json"

    def "should convert complex"() {
        given:
        def expected = new Afm(
                [new AttributeItem(new UriObjQualifier("/uri/to/displayForm/1"), "attribute1", "attributeAlias")],
                [
                        new PositiveAttributeFilter(new UriObjQualifier("/uri/to/displayForm/3"), ["ab", "cd"]),
                        new AbsoluteDateFilter(new UriObjQualifier("/uri/to/dataSet/1"), new LocalDate("2000-08-30"), new LocalDate("2017-08-07"))
                ],
                [
                        new MeasureItem(new VOSimpleMeasureDefinition(new UriObjQualifier("/uri/to/measure/0"), null, null, []),
                                "measure0", "Measure 0 alias", null),
                        new MeasureItem(new VOSimpleMeasureDefinition(new UriObjQualifier("/uri/to/measure/1"), "sum", false,
                                [
                                        new PositiveAttributeFilter(new UriObjQualifier("/uri/to/displayForm/3"), []),
                                        new NegativeAttributeFilter(new UriObjQualifier("/uri/to/displayForm/3"), ["ab", "cd"]),
                                        new RelativeDateFilter(new UriObjQualifier("/uri/to/dataSet/2"), "month", 0, -11)
                                ]
                        ),
                                "measure1", "Measure 1 alias", null)
                ],
                null
        )
        VisualizationObject visualizationObject = readObjectFromResource("/$COMPLEX_VISUALIZATION", VisualizationObject)
        Afm converted = convertToAfm(visualizationObject)

        expect:
        that converted, jsonEquals(expected)
    }

    def "should convert simple"() {
        given:
        Afm expected = new Afm([], [], [], null)
        VisualizationObject visualizationObject = readObjectFromResource("/$SIMPLE_VISUALIZATION", VisualizationObject)
        Afm converted = convertToAfm(visualizationObject)

        expect:
        that converted, jsonEquals(expected)
    }


    @Unroll
    def "should generate result spec for table with default sorting from #name"() {
        given:
        VisualizationObject vo = readObjectFromResource("/$resource", VisualizationObject)
        Function getter = { vizObject ->
            Stub(VisualizationClass) {
                getVisualizationType() >> VisualizationType.TABLE
                getUri() >> vo.getVisualizationClassUri()
            }
        }
        ResultSpec converted = convertToResultSpec(vo, getter)

        expect:
        that converted, jsonEquals(expected)

        where:
        name         | resource                   | expected
        "measures"   | MULTIPLE_MEASURE_BUCKETS   | new ResultSpec(
                [new Dimension([]), new Dimension("measureGroup")],
                null
        )
        "attributes" | MULTIPLE_ATTRIBUTE_BUCKETS | new ResultSpec(
                [new Dimension(["attribute1", "attribute2", "attribute"], null)],
                null
        )
    }

    @Unroll
    def "should generate result spec for #type"() {
        given:
        VisualizationObject vo = readObjectFromResource("/$resource", VisualizationObject)
        Function getter = { vizObject ->
            Stub(VisualizationClass) {
                getVisualizationType() >> VisualizationType.of(type)
                getUri() >> vo.getVisualizationClassUri()
            }
        }
        ResultSpec converted = convertToResultSpec(vo, getter)

        expect:
        that converted, jsonEquals(expected)

        where:
        type   | resource             | expected
        "pie"  | STACKED_COLUMN_CHART | new ResultSpec(
                [new Dimension("measureGroup"), new Dimension("1")],
                null
        )
        "bar"  | STACKED_COLUMN_CHART | new ResultSpec(
                [new Dimension("2"), new Dimension(["1", "measureGroup"], null)],
                null
        )
        "line" | LINE_CHART           | new ResultSpec(
                [new Dimension("measureGroup"), new Dimension("1")],
                null
        )
    }

    @Unroll
    def "should return #value"() {
        List<SortItem> sorts = parseSorting(properties)

        expect:
        that sorts, jsonEquals(expected)

        where:
        value << [ "attribute sorting", "measure sorting"]
        properties << [
                '{"sortItems":[{"attributeSortItem":{"direction":"desc","attributeIdentifier":"id"}}]}',
                '{"sortItems":[{"measureSortItem":{"direction":"desc","locators":[{"measureLocatorItem":{"measureIdentifier":"id"}}]}}]}'
        ]
        expected << [
                [new AttributeSortItem("desc", "id")],
                [new MeasureSortItem("desc", [new MeasureLocatorItem("id")])]
        ]
    }

    @Unroll
    def "should throw JsonParseException"() {
        when:
        parseSorting(properties)

        then:
        thrown(JsonParseException)

        where:
        properties << ['abcd', '{"sortItems":[{"someinvalidstring"}]}']
    }

    def "should fail when incorrect visualization class is provided"() {
        when:
        convertToResultSpec(
                Stub(VisualizationObject) { getVisualizationClassUri() >> 'visClassUri'},
                Stub(VisualizationClass) { getUri() >> 'nonMatchingVisClassUri'}
        )

        then:
        thrown(IllegalArgumentException)
    }

    @Unroll
    def "should fail if any argument is null in convertToResultSpec(VisualizationObject, VisualizationClass)"() {
        when:
        convertToResultSpec(vo, vc)

        then:
        thrown(IllegalArgumentException)

        where:
        vo << [null, Stub(VisualizationObject), null]
        vc << [null, null, Stub(VisualizationClass)]
    }

    @Unroll
    def "should fail if any argument is null in convertToResultSpec(VisualizationObject, Function)"() {
        when:
        convertToResultSpec(vo, vcg)

        then:
        thrown(IllegalArgumentException)

        where:
        vo << [null, Stub(VisualizationObject), null]
        vcg << [null, null, Stub(Function)]
    }

    @Unroll
    def "should fail if any argument is null in convertToExecution(VisualizationObject, VisualizationClass)"() {
        when:
        convertToExecution(vo, vc)

        then:
        thrown(IllegalArgumentException)

        where:
        vo << [null, Stub(VisualizationObject), null]
        vc << [null, null, Stub(VisualizationClass)]
    }

    @Unroll
    def "should fail if any argument is null in convertToExecution(VisualizationObject, Function)"() {
        when:
        convertToExecution(vo, vcg)

        then:
        thrown(IllegalArgumentException)

        where:
        vo << [null, Stub(VisualizationObject), null]
        vcg << [null, null, Stub(Function)]
    }
}
