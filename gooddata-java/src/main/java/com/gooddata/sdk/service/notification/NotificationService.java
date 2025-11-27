/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.notification;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.model.notification.Channel;
import com.gooddata.sdk.model.notification.ProjectEvent;
import com.gooddata.sdk.model.notification.Subscription;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.GoodDataSettings;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

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
     * @param event   event to trigger
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

    /**
     * Create channel for notifications
     *
     * @param account to create notifications on
     * @param channel configuration of channel
     * @return created channel
     */
    public Channel createChannel(final Account account, final Channel channel) {
        notNull(account, "account");
        notNull(channel, "channel");
        notEmpty(account.getId(), "account.id");

        try {
            return restTemplate.postForObject(Channel.URI, channel, Channel.class, account.getId());
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to create channel", e);
        }
    }

    /**
     * Remove channel
     *
     * @param channel to delete
     */
    public void removeChannel(final Channel channel) {
        notNull(channel, "channel");
        notNull(channel.getMeta(), "channel.meta");
        notEmpty(channel.getMeta().getUri(), "channel.meta.uri");

        try {
            restTemplate.delete(channel.getMeta().getUri());
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to delete channel", e);
        }
    }

    /**
     * Create subscription for notifications
     *
     * @param project      to create subscription on
     * @param account      to create subscription for
     * @param subscription to create
     * @return created subscription
     */
    public Subscription createSubscription(final Project project, final Account account, final Subscription subscription) {
        notNull(project, "project");
        notNull(account, "account");
        notNull(subscription, "subscription");
        notEmpty(project.getId(), "project.id");
        notEmpty(account.getId(), "account.id");

        try {
            return restTemplate.postForObject(Subscription.URI, subscription, Subscription.class, project.getId(), account.getId());
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to create subscription", e);
        }
    }

    /**
     * Remove subscription
     *
     * @param subscription to delete
     */
    public void removeSubscription(final Subscription subscription) {
        notNull(subscription, "subscription");
        notNull(subscription.getMeta(), "subscription.meta");
        notEmpty(subscription.getMeta().getUri(), "subscription.meta.uri");

        try {
            restTemplate.delete(subscription.getMeta().getUri());
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to delete subscription", e);
        }
    }
}
