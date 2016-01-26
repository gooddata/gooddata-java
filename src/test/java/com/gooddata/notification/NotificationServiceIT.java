/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.notification;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static java.util.Collections.singletonMap;
import static net.jadler.Jadler.onRequest;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.project.Project;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class NotificationServiceIT extends AbstractGoodDataIT {

    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String PROJECT_EVENT_PATH = ProjectEvent.TEMPLATE.expand(PROJECT_ID).toString();

    private Project project;

    @BeforeClass
    public void setUp() throws Exception {
        project = MAPPER.readValue(readFromResource("/project/project.json"), Project.class);
    }

    @Test
    public void shouldTriggerEvent() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PROJECT_EVENT_PATH)
            .respond()
                .withStatus(204);

        gd.getNotificationService().triggerEvent(project, new ProjectEvent("type", singletonMap("key", "value")));
    }
}