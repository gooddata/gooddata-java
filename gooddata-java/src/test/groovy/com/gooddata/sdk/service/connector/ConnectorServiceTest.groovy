/*
 * Copyright (C) 2004-2021, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.service.connector

import com.gooddata.sdk.model.connector.Reload
import com.gooddata.sdk.service.GoodDataSettings
import com.gooddata.sdk.service.project.ProjectService
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Subject

import static java.util.Collections.emptyMap

class ConnectorServiceTest extends Specification {

    @Subject
    def service = new ConnectorService(Mock(RestTemplate), Mock(ProjectService), Mock(GoodDataSettings))

    def "getZendesk4Reload should throw when Reload is null"() {
        when:
        service.getZendesk4Reload(null)

        then:
        thrown(IllegalArgumentException)
    }

    def "getZendesk4Reload should throw when Reload does not contain self URI"() {
        when:
        service.getZendesk4Reload(new Reload(emptyMap()))

        then:
        thrown(IllegalArgumentException)
    }
}