/*
 * (C) 2023 GoodData Corporation.
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

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException; 

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Service to trigger and manage notifications.
 */
public class NotificationService extends AbstractService {

    public NotificationService(final WebClient webClient, final GoodDataSettings settings) {
        super(webClient, settings);
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
            webClient.post()
                .uri(uriBuilder -> uriBuilder.path(ProjectEvent.URI).build(project.getId())) 
                .bodyValue(event) 
                .retrieve() 
                .toBodilessEntity() 
                .block(); 
        } catch (WebClientResponseException | GoodDataRestException e) {
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
            return webClient.post() 
                .uri(uriBuilder -> uriBuilder.path(Channel.URI).build(account.getId())) 
                .bodyValue(channel) 
                .retrieve() 
                .bodyToMono(Channel.class) 
                .block(); 
        } catch (WebClientResponseException | GoodDataRestException e) { 
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
            webClient.delete() 
                .uri(channel.getMeta().getUri()) 
                .retrieve() 
                .toBodilessEntity() 
                .block(); 
        } catch (WebClientResponseException | GoodDataRestException e) {
            throw new GoodDataException("Unable to delete channel", e);
        }
    }

    /**
     * Create subscription for notifications
     *
     * @param project to create subscription on
     * @param account to create subscription for
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
            return webClient.post() 
                .uri(uriBuilder -> uriBuilder.path(Subscription.URI).build(project.getId(), account.getId())) 
                .bodyValue(subscription) 
                .retrieve() 
                .bodyToMono(Subscription.class) 
                .block();
        } catch (WebClientResponseException | GoodDataRestException e) {
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
            webClient.delete() 
                .uri(subscription.getMeta().getUri()) 
                .retrieve() 
                .toBodilessEntity()
                .block();
        } catch (WebClientResponseException | GoodDataRestException e) { 
            throw new GoodDataException("Unable to delete subscription", e);
        }
    }
}
