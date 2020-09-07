/*
 * Copyright (C) 2007-2020, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.executeafm.afm.filter

import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier
import com.gooddata.sdk.model.executeafm.LocalIdentifierQualifier
import com.gooddata.sdk.model.executeafm.UriObjQualifier
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class RankingFilterTest extends Specification {
    private static final String TOP_RANKING_FILTER_JSON = 'executeafm/afm/rankingFilter.json'
    private static final String BOTTOM_RANKING_FILTER_JSON = 'executeafm/afm/rankingFilterWithAttributes.json'

    def "should serialize TOP ranking filter"() {
        expect:
        that new RankingFilter(
                [new LocalIdentifierQualifier('m1')],
                null,
                RankingFilterOperator.TOP,
                10
        ), jsonEquals(resource(TOP_RANKING_FILTER_JSON))
    }

    def "should serialize BOTTOM ranking filter"() {
        expect:
        that new RankingFilter(
                [new LocalIdentifierQualifier('m1'), new IdentifierObjQualifier('id2')],
                [new LocalIdentifierQualifier('a1'), new UriObjQualifier('/mockProject/obj/2')],
                RankingFilterOperator.BOTTOM,
                5
        ), jsonEquals(resource(BOTTOM_RANKING_FILTER_JSON))
    }

    def "should deserialize filter with TOP ranking type"() {
        when:
        RankingFilter rankingFilter = readObjectFromResource("/$TOP_RANKING_FILTER_JSON", RankingFilter)

        then:
        RankingFilter.isInstance(rankingFilter)
        rankingFilter.measures == [
                new LocalIdentifierQualifier('m1')
        ]
        rankingFilter.getOperatorAsString() == 'TOP'
        rankingFilter.getOperator() == RankingFilterOperator.TOP
        rankingFilter.getValue() == 10
        rankingFilter.toString()
    }

    def "should deserialize filter with BOTTOM ranking type"() {
        when:
        RankingFilter rankingFilter = readObjectFromResource("/$BOTTOM_RANKING_FILTER_JSON", RankingFilter)

        then:
        RankingFilter.isInstance(rankingFilter)
        rankingFilter.measures == [
                new LocalIdentifierQualifier('m1'),
                new IdentifierObjQualifier('id2')
        ]
        rankingFilter.attributes == [
                new LocalIdentifierQualifier('a1'),
                new UriObjQualifier('/mockProject/obj/2')
        ]
        rankingFilter.getOperatorAsString() == 'BOTTOM'
        rankingFilter.getOperator() == RankingFilterOperator.BOTTOM
        rankingFilter.getValue() == 5
        rankingFilter.toString()
    }

    def "should copy with uri converter"() {
        when:
        RankingFilter filter = new RankingFilter([
                new LocalIdentifierQualifier('m1'),
                new IdentifierObjQualifier('id1'),
                new IdentifierObjQualifier('id2'),
                new UriObjQualifier('/gdc/md/projectId/obj/4')
        ], [
                new IdentifierObjQualifier('id3'),
                new UriObjQualifier('/gdc/md/projectId/obj/5'),
                new LocalIdentifierQualifier('a1')
        ],
                RankingFilterOperator.TOP,
                3
        )
        def qualifiersConversionMap = [
                (new IdentifierObjQualifier('id2')): new UriObjQualifier('/gdc/md/projectId/obj/2'),
                (new IdentifierObjQualifier('id1')): new UriObjQualifier('/gdc/md/projectId/obj/1'),
                (new IdentifierObjQualifier('id3')): new UriObjQualifier('/gdc/md/projectId/obj/3')
        ]
        def copy = filter.withObjUriQualifiers({ identifierQualifier ->
            return Optional.ofNullable(qualifiersConversionMap.get(identifierQualifier))
        })

        then:
        copy.measures == [
                new LocalIdentifierQualifier('m1'),
                new UriObjQualifier('/gdc/md/projectId/obj/1'),
                new UriObjQualifier('/gdc/md/projectId/obj/2'),
                new UriObjQualifier('/gdc/md/projectId/obj/4')
        ]
        copy.attributes == [
                new UriObjQualifier('/gdc/md/projectId/obj/3'),
                new UriObjQualifier('/gdc/md/projectId/obj/5'),
                new LocalIdentifierQualifier('a1'),
        ]
        copy.operator == RankingFilterOperator.TOP
        copy.value == 3
    }

    def "should copy with uri converter when attributes are null"() {
        when:
        RankingFilter filter = new RankingFilter([
                new LocalIdentifierQualifier('m1'),
                new IdentifierObjQualifier('id1'),
        ],
                null,
                RankingFilterOperator.TOP,
                3
        )
        def qualifiersConversionMap = [
                (new IdentifierObjQualifier('id1')): new UriObjQualifier('/gdc/md/projectId/obj/1'),
        ]
        def copy = filter.withObjUriQualifiers({ identifierQualifier ->
            return Optional.ofNullable(qualifiersConversionMap.get(identifierQualifier))
        })

        then:
        copy.measures == [
                new LocalIdentifierQualifier('m1'),
                new UriObjQualifier('/gdc/md/projectId/obj/1')
        ]
        copy.attributes == null
        copy.operator == RankingFilterOperator.TOP
        copy.value == 3
    }

    def "should fail when qualifier converter is not provided or cannot convert object's identifier qualifiers"() {
        when:
        RankingFilter filter = new RankingFilter([
                new IdentifierObjQualifier('id1'),
        ],
                [
                        new LocalIdentifierQualifier('a1'),
                ],
                RankingFilterOperator.TOP,
                3
        )
        filter.withObjUriQualifiers(invalidObjQualifierConverter)

        then:
        thrown(IllegalArgumentException)

        where:
        invalidObjQualifierConverter << [null, { identifierQualifier -> Optional.empty() }]
    }

    def "should get all used obj qualifiers"() {
        when:
        RankingFilter filter = new RankingFilter([
                new LocalIdentifierQualifier('m1'),
                new IdentifierObjQualifier('id1'),
                new UriObjQualifier('/gdc/md/projectId/obj/4')
        ], [
                new IdentifierObjQualifier('id3'),
                new UriObjQualifier('/gdc/md/projectId/obj/5'),
                new LocalIdentifierQualifier('a1')
        ],
                RankingFilterOperator.TOP,
                3
        )
        def qualifiers = filter.getObjQualifiers()

        then:
        qualifiers.containsAll([
                new IdentifierObjQualifier('id1'),
                new UriObjQualifier('/gdc/md/projectId/obj/4'),
                new IdentifierObjQualifier('id3'),
                new UriObjQualifier('/gdc/md/projectId/obj/5')
        ])
    }

    def "should get all used obj qualifiers when attributes are null"() {
        when:
        RankingFilter filter = new RankingFilter([
                new LocalIdentifierQualifier('m1'),
                new IdentifierObjQualifier('id1'),
                new UriObjQualifier('/gdc/md/projectId/obj/4')
        ],
                null,
                RankingFilterOperator.TOP,
                3
        )
        def qualifiers = filter.getObjQualifiers()

        then:
        qualifiers.containsAll([
                new IdentifierObjQualifier('id1'),
                new UriObjQualifier('/gdc/md/projectId/obj/4')
        ])
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(RankingFilter).usingGetClass().verify()
    }

    def "getOperator should throw when created with unknown operator"() {
        given:
        RankingFilter filter = new RankingFilter([new LocalIdentifierQualifier('m1')], null, 'UNKNOWN', 10)

        when:
        filter.getOperator()

        then:
        thrown(UnsupportedOperationException)
    }

    @Unroll
    def "getOperator should return enum value #operator when name '#name' is provided to constructor"() {
        when:
        RankingFilter filter = new RankingFilter([new LocalIdentifierQualifier('m1')], null, name, 10)

        then:
        filter.getOperator() == operator
        filter.getOperator().toString() == name

        where:
        operator                     | name
        RankingFilterOperator.TOP    | 'TOP'
        RankingFilterOperator.BOTTOM | 'BOTTOM'
    }
}
