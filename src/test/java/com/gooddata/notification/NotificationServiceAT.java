/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
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