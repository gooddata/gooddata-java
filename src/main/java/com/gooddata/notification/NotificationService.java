/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.notification;

import static com.gooddata.util.Validate.notNull;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.GoodDataSettings;
import com.gooddata.project.Project;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service to trigger and manage notifications.
 */
public class NotificationService extends AbstractService {

    public NotificationService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        super(restTemplate, settings);
    }

    /**
     * Triggers given project event.
     *
     * @param project project of the event
     * @param event event to trigger
     */
    public void triggerEvent(final Project project, final ProjectEvent event) {
        notNull(project, "project");
        notNull(event, "event");
        try {
            restTemplate.postForEntity(ProjectEvent.URI, event, Void.class, project.getId());
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to post project event.", e);
        }
    }
}
