/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.visualization

import com.gooddata.sdk.model.md.Meta
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Shared
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static spock.util.matcher.HamcrestSupport.that

class VisualizationClassTest extends Specification {
    private static final String TABLE_VISUALIZATION_CLASS = '/md/visualization/tableVisualizationClass.json'
    private static final String BAR_VISUALIZATION_CLASS = '/md/visualization/barVisualizationClass.json'
    private static final String EXTERNAL_VISUALIZATION_CLASS = '/md/visualization/externalVisualizationClass.json'

    @Shared
    VisualizationClass tableVisualizationClass = readObjectFromResource(TABLE_VISUALIZATION_CLASS, VisualizationClass)

    def "should serialize full"() {
        VisualizationClass external = readObjectFromResource(EXTERNAL_VISUALIZATION_CLASS, VisualizationClass)

        expect:
        that new VisualizationClass(
                new VisualizationClass.Content("local:table", "icon", "iconSelected", "checksum", 0),
                new Meta("visClass")
        ),
                jsonEquals(tableVisualizationClass)

        and:
        that new VisualizationClass(
                new VisualizationClass.Content("https://some.vis", "icon", "iconSelected", "checksum", 0),
                new Meta("external")
        ),
                jsonEquals(external)
    }

    def "should check if visualization is local"() {
        VisualizationClass visualizationClass = readObjectFromResource(resource, VisualizationClass)

        expect:
        visualizationClass.isLocal() == expected

        where:
        resource                     | expected
        EXTERNAL_VISUALIZATION_CLASS | false
        TABLE_VISUALIZATION_CLASS    | true
    }

    def "should return correct visualization type"() {
        VisualizationClass visualizationClass = readObjectFromResource(resource, VisualizationClass)

        expect:
        visualizationClass.getVisualizationType() == expected

        where:
        resource                     | expected
        EXTERNAL_VISUALIZATION_CLASS | VisualizationType.TABLE
        TABLE_VISUALIZATION_CLASS    | VisualizationType.TABLE
        BAR_VISUALIZATION_CLASS      | VisualizationType.BAR
    }

    def "test serializable"() {
        VisualizationClass deserialized = SerializationUtils.roundtrip(tableVisualizationClass)

        expect:
        that deserialized, jsonEquals(tableVisualizationClass)
    }
}

