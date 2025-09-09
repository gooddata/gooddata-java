/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard.filter

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static com.gooddata.sdk.common.util.ResourceUtils.readStringFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static spock.util.matcher.HamcrestSupport.that

class AttributeFilterReferenceTest extends Specification {

    def "deserializes from JSON"() {
        when:
        def reference = readObjectFromResource('/md/dashboard/filter/attributeFilterReference.json', AttributeFilterReference)

        then:
        reference.displayFormUri == '/display/form/1'
    }

    def "creates and serializes to JSON"() {
        when:
        def reference = new AttributeFilterReference('/display/form/1')

        then:
        that reference, jsonEquals(readStringFromResource('/md/dashboard/filter/attributeFilterReference.json'))
    }
}

