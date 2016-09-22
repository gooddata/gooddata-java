/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.notification;

import static java.util.Collections.singletonMap;

import com.gooddata.AbstractGoodDataAT;
import org.testng.annotations.Test;

public class NotificationServiceAT extends AbstractGoodDataAT {

    @Test(groups = "notification", dependsOnGroups = "project")
    public void triggerProjectEvent() throws Exception {
        gd.getNotificationService().triggerEvent(project, new ProjectEvent("sdk.event.test", singletonMap("test", "map")));
    }

}