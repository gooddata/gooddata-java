/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.visualization

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.gooddata.sdk.model.executeafm.Execution
import com.gooddata.sdk.model.executeafm.LocalIdentifierQualifier
import com.gooddata.sdk.model.executeafm.UriObjQualifier
import com.gooddata.sdk.model.executeafm.afm.filter.AbsoluteDateFilter
import com.gooddata.sdk.model.executeafm.afm.Afm
import com.gooddata.sdk.model.executeafm.afm.AttributeItem
import com.gooddata.sdk.model.executeafm.afm.filter.ComparisonCondition
import com.gooddata.sdk.model.executeafm.afm.filter.ComparisonConditionOperator
import com.gooddata.sdk.model.executeafm.afm.MeasureItem
import com.gooddata.sdk.model.executeafm.afm.filter.MeasureValueFilter
import com.gooddata.sdk.model.executeafm.afm.filter.NegativeAttributeFilter
import com.gooddata.sdk.model.executeafm.afm.filter.PositiveAttributeFilter
import com.gooddata.sdk.model.executeafm.afm.filter.RankingFilter
import com.gooddata.sdk.model.executeafm.afm.filter.RelativeDateFilter
import com.gooddata.sdk.model.executeafm.afm.filter.UriAttributeFilterElements
import com.gooddata.sdk.model.executeafm.resultspec.AttributeSortItem
import com.gooddata.sdk.model.executeafm.resultspec.Dimension
import com.gooddata.sdk.model.executeafm.resultspec.MeasureLocatorItem
import com.gooddata.sdk.model.executeafm.resultspec.MeasureSortItem
import com.gooddata.sdk.model.executeafm.resultspec.ResultSpec
import com.gooddata.sdk.model.executeafm.resultspec.SortItem
import com.gooddata.sdk.model.executeafm.resultspec.TotalItem
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.util.function.Function

import static VisualizationConverter.convertToAfm
import static VisualizationConverter.convertToResultSpec
import static VisualizationConverter.parseSorting
import static com.gooddata.sdk.model.md.visualization.VisualizationConverter.convertToAfmWithNativeTotals
import static com.gooddata.sdk.model.md.visualization.VisualizationConverter.convertToExecution
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static com.gooddata.sdk.model.md.visualization.VisualizationConverter.convertToResultSpecWithTotals
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static spock.util.matcher.HamcrestSupport.expect
import static spock.util.matcher.HamcrestSupport.that

class VisualizationConverterTest extends Specification {
    private static final String COMPLEX_VISUALIZATION = "md/visualization/complexVisualizationObject.json"
    private static final String SIMPLE_VISUALIZATION = "md/visualization/simpleVisualizationObject.json"

    private static final String MULTIPLE_MEASURE_BUCKETS = "md/visualization/multipleMeasureBucketsVisualization.json"
    private static final String MULTIPLE_ATTRIBUTE_BUCKETS = "md/visualization/multipleAttributeBucketsVisualization.json"
    private static final String STACKED_COLUMN_CHART = "md/visualization/stackedColumnChart.json"
    private static final String LINE_CHART = "md/visualization/lineChart.json"
    private static final String TABLE_WITH_TOTALS = "md/visualization/complexTableWithTotals.json"
    private static final String AFM_FROM_TABLE_WITH_TOTALS = "executeafm/afm/complextTableWithTotalsConvertedAfm.json"
    private static final String EXECUTION_FROM_TABLE_WITH_TOTALS = "executeafm/executionComplexTableConverted.json"

    @SuppressWarnings("GrDeprecatedAPIUsage")
    def "should convert complex"() {
        given:
        Afm expectedAfm = new Afm(
                [new AttributeItem(new UriObjQualifier("/uri/to/displayForm/1"), "attribute1", "attributeAlias")],
                [
                        new PositiveAttributeFilter(new UriObjQualifier("/uri/to/displayForm/3"), new UriAttributeFilterElements(["ab", "cd"])),
                        new AbsoluteDateFilter(new UriObjQualifier("/uri/to/dataSet/1"), LocalDate.of(2000, 8, 30), LocalDate.of(2017, 8, 07)),
                        new MeasureValueFilter(new LocalIdentifierQualifier("measure0"), new ComparisonCondition(ComparisonConditionOperator.GREATER_THAN, 200.1)),
                        new RankingFilter([new LocalIdentifierQualifier("measure0")], null, "TOP", 3)
                ],
                [
                        new MeasureItem(new VOSimpleMeasureDefinition(new UriObjQualifier("/uri/to/measure/0"), null, null, []),
                                "measure0", "Measure 0 alias", null),
                        new MeasureItem(new VOSimpleMeasureDefinition(new UriObjQualifier("/uri/to/measure/1"), "sum", false,
                                [
                                        new PositiveAttributeFilter(new UriObjQualifier("/uri/to/displayForm/3"), new UriAttributeFilterElements([])),
                                        new NegativeAttributeFilter(new UriObjQualifier("/uri/to/displayForm/3"), new UriAttributeFilterElements(["ab", "cd"])),
                                        new RelativeDateFilter(new UriObjQualifier("/uri/to/dataSet/2"), "month", 0, -11)
                                ]
                        ),
                                "measure1", "Measure 1 alias", null)
                ],
                []
        )
        VisualizationObject visualizationObject = readObjectFromResource("/$COMPLEX_VISUALIZATION", VisualizationObject)
        Afm converted = convertToAfm(visualizationObject)

        expect:
        that converted, jsonEquals(expectedAfm)
    }

    def "should convert simple"() {
        given:
        Afm expectedAfm = new Afm([], [], [], [])
        VisualizationObject visualizationObject = readObjectFromResource("/$SIMPLE_VISUALIZATION", VisualizationObject)
        Afm converted = convertToAfm(visualizationObject)

        expect:
        that converted, jsonEquals(expectedAfm)
    }

    def "should convert AFM of complex pivot table with totals"() {
        given:
        Afm expectedAfm = readObjectFromResource("/$AFM_FROM_TABLE_WITH_TOTALS", Afm)
        VisualizationObject visualizationObject = readObjectFromResource("/$TABLE_WITH_TOTALS", VisualizationObject)
        Afm converted = convertToAfmWithNativeTotals(visualizationObject)

        expect:
        that converted, jsonEquals(expectedAfm)
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
        that converted, jsonEquals(expectedResultSpec)

        where:
        name         | resource                   | expectedResultSpec
        "measures"   | MULTIPLE_MEASURE_BUCKETS   | new ResultSpec(
                [new Dimension([]), new Dimension("measureGroup")],
                null
        )
        "attributes" | MULTIPLE_ATTRIBUTE_BUCKETS | new ResultSpec(
                [new Dimension(["attribute1", "attribute2", "attribute"], null)],
                null
        )
    }

    def "should generate result spec for complex table with totals"() {
        when:
        VisualizationObject vo = readObjectFromResource("/$TABLE_WITH_TOTALS", VisualizationObject)
        Function getter = { vizObject ->
            Stub(VisualizationClass) {
                getVisualizationType() >> VisualizationType.TABLE
                getUri() >> vo.getVisualizationClassUri()
            }
        }
        ResultSpec converted = convertToResultSpecWithTotals(vo, getter)
        then:
        that converted, jsonEquals(
                // pivot table is simplified to a basic table (2 dimensions: measureGroup and attributes)
                new ResultSpec([
                        new Dimension(
                                [
                                        "e4bb25477bca4fb2a29a4b80d94568d4",
                                        "9008f5d33b3e41279402a25e2f05d0c9",
                                        "023641d306f84921be39d0aa1d6464db",
                                        "a22843f5d77f48b4938ccfb460eb8be4",
                                        "a77983fcc9574f6bad6be1d3cb08bf71"
                                ],
                                [
                                        new TotalItem("fd0164f14ec2444b9b5a7140ce059036", "nat", "e4bb25477bca4fb2a29a4b80d94568d4"),
                                        new TotalItem("fd0164f14ec2444b9b5a7140ce059036", "nat", "9008f5d33b3e41279402a25e2f05d0c9"),
                                        new TotalItem("fd0164f14ec2444b9b5a7140ce059036", "nat", "a22843f5d77f48b4938ccfb460eb8be4"),
                                        new TotalItem("fd0164f14ec2444b9b5a7140ce059036", "nat", "a77983fcc9574f6bad6be1d3cb08bf71"),
                                        new TotalItem("fd0164f14ec2444b9b5a7140ce059036", "sum", "e4bb25477bca4fb2a29a4b80d94568d4"),
                                        new TotalItem("fd0164f14ec2444b9b5a7140ce059036", "nat", "023641d306f84921be39d0aa1d6464db")
                                ] as Set<TotalItem>
                        ),
                        new Dimension(["measureGroup"], null)
                ], [new AttributeSortItem("asc", "e4bb25477bca4fb2a29a4b80d94568d4", null)])
        )
    }

    def "should generate execution for complex table with totals"() {
        given:
        Execution expectedExecution = readObjectFromResource("/$EXECUTION_FROM_TABLE_WITH_TOTALS", Execution)
        VisualizationObject vo = readObjectFromResource("/$TABLE_WITH_TOTALS", VisualizationObject)
        Function getter = { vizObject ->
            Stub(VisualizationClass) {
                getVisualizationType() >> VisualizationType.TABLE
                getUri() >> vo.getVisualizationClassUri()
            }
        }
        Execution converted = VisualizationConverter.convertToExecutionWithTotals(vo, getter)

        expect:
        that converted, jsonEquals(expectedExecution)
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
        that converted, jsonEquals(expectedResultSpec)

        where:
        type   | resource             | expectedResultSpec
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
        that sorts, jsonEquals(expectedSorts)

        where:
        value << [ "attribute sorting", "measure sorting"]
        properties << [
                '{"sortItems":[{"attributeSortItem":{"direction":"desc","attributeIdentifier":"id"}}]}',
                '{"sortItems":[{"measureSortItem":{"direction":"desc","locators":[{"measureLocatorItem":{"measureIdentifier":"id"}}]}}]}'
        ]
        expectedSorts << [
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
