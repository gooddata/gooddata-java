/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.visualization

import com.gooddata.sdk.model.executeafm.UriObjQualifier
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that


class VisualizationAttributeTest extends Specification {

    static final String JSON = 'md/visualization/visualizationAttribute.json'
    static final String JSON_FULL = 'md/visualization/visualizationAttributeFull.json'

    def "should serialize"() {
        expect:
        that new VisualizationAttribute(new UriObjQualifier('/some/uri'), 'li1'), jsonEquals(resource(JSON))
    }

    def "should serialize full"() {
        expect:
        that new VisualizationAttribute(new UriObjQualifier('/some/uri'), 'li1', 'Some alias'), jsonEquals(resource(JSON_FULL))
    }

    def "should deserialize"() {
        when:
        def vizAttribute = readObjectFromResource("/$JSON_FULL".toString(), VisualizationAttribute)

        then:
        vizAttribute?.displayForm?.uri == '/some/uri'
        vizAttribute?.localIdentifier == 'li1'
        vizAttribute?.alias == 'Some alias'
    }
}
