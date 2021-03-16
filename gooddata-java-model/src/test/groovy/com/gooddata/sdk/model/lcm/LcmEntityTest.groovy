/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.lcm

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class LcmEntityTest extends Specification {

    private static final String LCM_ENTITY_JSON = 'lcm/lcmEntity.json'
    private static final String LCM_ENTITY_FULL_JSON = 'lcm/lcmEntityFull.json'

    def "should serialize"() {
        expect:
        that new LcmEntity('PROJECT_ID', 'PROJECT_TITLE', [project: '/gdc/projects/PROJECT_ID']),
                jsonEquals(resource(LCM_ENTITY_JSON))
    }

    def "should serialize full"() {
        expect:
        that new LcmEntity('PROJECT_ID', 'PROJECT_TITLE', 'CLIENT', 'SEGMENT', 'DATA_PRODUCT',
                [
                        project: '/gdc/projects/PROJECT_ID',
                        client: '/gdc/domains/default/dataproducts/DATA_PRODUCT/clients/CLIENT',
                        segment: '/gdc/domains/default/dataproducts/DATA_PRODUCT/segments/SEGMENT',
                        dataProduct: '/gdc/domains/default/dataproducts/DATA_PRODUCT'
                ]),
                jsonEquals(resource(LCM_ENTITY_FULL_JSON))
    }

    def "should deserialize"() {
        when:
        LcmEntity lcmEntity = readObjectFromResource("/$LCM_ENTITY_JSON", LcmEntity)

        then:
        lcmEntity.projectId == 'PROJECT_ID'
        lcmEntity.projectTitle == 'PROJECT_TITLE'
        lcmEntity.links.size() == 1
        lcmEntity.projectUri == '/gdc/projects/PROJECT_ID'
    }

    def "should deserialize full"() {
        when:
        LcmEntity lcmEntity = readObjectFromResource("/$LCM_ENTITY_FULL_JSON", LcmEntity)

        then:
        lcmEntity.projectId == 'PROJECT_ID'
        lcmEntity.projectTitle == 'PROJECT_TITLE'
        lcmEntity.clientId == 'CLIENT'
        lcmEntity.segmentId == 'SEGMENT'
        lcmEntity.dataProductId == 'DATA_PRODUCT'
        lcmEntity.links.size() == 4
        lcmEntity.projectUri == '/gdc/projects/PROJECT_ID'
        lcmEntity.clientUri == '/gdc/domains/default/dataproducts/DATA_PRODUCT/clients/CLIENT'
        lcmEntity.segmentUri == '/gdc/domains/default/dataproducts/DATA_PRODUCT/segments/SEGMENT'
        lcmEntity.dataProductUri == '/gdc/domains/default/dataproducts/DATA_PRODUCT'
        lcmEntity.toString()
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(LcmEntity).usingGetClass().verify()
    }
}
