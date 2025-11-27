/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service

import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class GoodDataBeansAsServicesIT extends Specification {

    @Shared
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext('spring/gd-annotationConfig.xml')

    @Unroll
    def "should register service of type #clazzName from xml using annotation config"() {
        expect:
        context.getBean(clazz)

        where:
        clazz << GoodData.class.getDeclaredMethods()
                .collect { m -> m.returnType }
                .findAll { rt -> rt.name.endsWith('Service') }
        clazzName = clazz.simpleName
    }
}
