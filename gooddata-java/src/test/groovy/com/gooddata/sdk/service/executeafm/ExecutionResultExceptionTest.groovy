/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.executeafm

import com.gooddata.sdk.common.GoodDataRestException
import com.gooddata.sdk.service.executeafm.ExecutionResultException
import spock.lang.Specification
import spock.lang.Unroll


class ExecutionResultExceptionTest extends Specification {

    @Unroll
    def "should create with status #code"() {
        expect:
        new ExecutionResultException(new GoodDataRestException(code, 'reqId', 'status', null))
            .message ==~ msgPattern

        where:
        code | msgPattern
        400  | /.*is not computable.*/
        410  | /.*result no longer available.*/
        413  | /.*result is too large.*/
        500  | /.*failed.*unknown.*reason.*/
    }
}
