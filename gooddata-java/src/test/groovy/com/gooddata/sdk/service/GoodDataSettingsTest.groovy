/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification
import spock.lang.Unroll

class GoodDataSettingsTest extends Specification {

    GoodDataSettings settings

    void setup() {
        settings = new GoodDataSettings()
    }

    def "should have defaults"() {
        expect:
        with(settings) {
            maxConnections > 0
            connectionTimeout >= 0
            connectionRequestTimeout >= 0
            socketTimeout >= 0
            pollSleep >= 0
            goodDataUserAgent =~ /GoodData-Java-SDK\/UNKNOWN \(.*\) Apache-HttpClient\/\d\.\d\.\d/
            presetHeaders['Accept'] == 'application/json'
            presetHeaders.containsKey('X-GDC-Version')
        }
    }

    @Unroll
    def "should set #name seconds"() {
        when:
        settings."${name}Seconds" = 53

        then:
        settings."$name" == 53000

        where:
        name << [ 'connectionTimeout', 'connectionRequestTimeout', 'socketTimeout', 'pollSleep' ]
    }

    @Unroll
    def "should not set #name invalid value"() {
        when:
        settings."set$name"(value)

        then:
        thrown(IllegalArgumentException)

        where:
        name                       | value
        'ConnectionTimeout'        | -3
        'ConnectionRequestTimeout' | -1
        'SocketTimeout'            | -5
        'PollSleep'                | -5
        'MaxConnections'           | 0
    }

    def "custom user agent should be prefix of default"() {
        given:
        GoodDataSettings defSettings = new GoodDataSettings()
        def defaultAgent = defSettings.userAgent

        when:
        defSettings.userAgent = 'customAgent/X.Y'

        then:
        defSettings.goodDataUserAgent.startsWith('customAgent/X.Y')
        defSettings.goodDataUserAgent.contains(defaultAgent)
    }

    def "should set preset header"() {
        when:
        settings.setPresetHeader('MyCustomHeader', 'myVal')

        then:
        settings.presetHeaders['MyCustomHeader'] == 'myVal'
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(GoodDataSettings)
                .usingGetClass()
                .suppress(Warning.NONFINAL_FIELDS)
                .verify()
    }
}
