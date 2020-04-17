/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.executeafm

import com.gooddata.sdk.model.executeafm.afm.filter.ExpressionFilter
import com.gooddata.sdk.model.executeafm.resultspec.AttributeSortItem
import com.gooddata.sdk.model.executeafm.resultspec.Dimension
import com.gooddata.sdk.model.executeafm.resultspec.ResultSpec
import com.gooddata.sdk.model.executeafm.resultspec.TotalItem
import com.gooddata.sdk.model.md.report.Total
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that


class VisualizationExecutionTest extends Specification {

    static final String VIZ_EXECUTION_JSON = 'executeafm/visualizationExecution.json'
    static final String VIZ_EXECUTION_FULL_JSON = 'executeafm/visualizationExecutionFull.json'

    def "should deserialize with reference only"() {
        when:
        def execution = readObjectFromResource("/$VIZ_EXECUTION_JSON", VisualizationExecution)

        then:
        execution?.reference == '/gdc/md/PROJECT/vizObjUri'
        !execution?.filters
        !execution?.resultSpec
    }

    def "should serialize"() {
        expect:
        that new VisualizationExecution(
                '/gdc/md/PROJECT/vizObjUri',
                [new ExpressionFilter('some expression')],
                new ResultSpec([new Dimension(['i1'], [new TotalItem('mId', Total.AVG, 'a1')].toSet())],
                [new AttributeSortItem('asc', 'aId')])
        ), jsonEquals(resource(VIZ_EXECUTION_FULL_JSON))
    }

    def "should deserialize"() {
        when:
        def execution = readObjectFromResource("/$VIZ_EXECUTION_FULL_JSON", VisualizationExecution)

        then:
        execution?.reference == '/gdc/md/PROJECT/vizObjUri'
        execution?.filters?.every { it.class == ExpressionFilter && it.value == 'some expression' }
        execution?.resultSpec?.sorts?.every { it instanceof AttributeSortItem && it.attributeIdentifier == 'aId' }
        execution?.resultSpec?.dimensions?.every { it.itemIdentifiers == ['i1'] && it.totals*.measureIdentifier == ['mId'] }
    }
}
