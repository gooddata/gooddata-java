/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report

import spock.lang.Specification

import static Total.*

class TotalTest extends Specification {
    def "should throw exception for invalid total name"() {
        when:
        Total.of("UnknownValue")

        then:
        def exception = thrown(UnsupportedOperationException)
        exception.message ==~ /.*"UnknownValue".*/
    }

    def "should match valid total values"() {
        expect:
        Total.values().each {
            it == Total.of(it.toString())
        }
    }

    def "should return ordered list of totals"() {
        expect:
        Total.orderedValues().size() == 6
        Total.orderedValues() == [SUM, MAX, MIN, AVG, MED, NAT]
    }
}