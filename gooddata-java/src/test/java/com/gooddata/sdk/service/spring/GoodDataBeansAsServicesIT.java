/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.spring;

import com.gooddata.sdk.service.GoodData;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.testng.Assert.*;

public class GoodDataBeansAsServicesIT {

    @Test
    public void servicesRegisteredFromXmlUsingAnnotationConfig() throws Exception {
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/gd-annotationConfig.xml");
        Arrays.stream(GoodData.class.getDeclaredMethods())
                .map(Method::getReturnType)
                .filter(rt -> rt.getName().endsWith("Service"))
                .forEach(rt -> assertNotNull(context.getBean(rt), "Bean of type " + rt + " should be registered"));
    }
}