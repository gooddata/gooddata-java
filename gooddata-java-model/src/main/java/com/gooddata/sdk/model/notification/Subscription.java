/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.notification;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.model.md.Meta;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Subscription for notifications
 */
@JsonTypeName("subscription")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscription {

    public static final String URI = "/gdc/projects/{project}/users/{user}/subscriptions";

    private final List<Trigger> triggers;
    private final TriggerCondition condition;
    private final MessageTemplate template;
    private final List<String> channels;
    private final Meta meta;


    /**
     * Creates Subscription
     *
     * @param triggers triggers of subscription
     * @param channels list of {@link Channel}
     * @param condition condition under which this subscription activates
     * @param template of message
     * @param title name of subscription
     */
    public Subscription(final List<Trigger> triggers,
                        final List<Channel> channels,
                        final TriggerCondition condition,
                        final MessageTemplate template,
                        final String title) {
        this(notNull(triggers, "triggers"),
             notNull(condition, "condition"),
             notNull(template, "template"),
             notNull(channels, "channels").stream().map(e -> e.getMeta().getUri()).collect(Collectors.toList()),
             new Meta(
                     notEmpty(title, "title")
             )
        );
    }

    @JsonCreator
    Subscription(@JsonProperty("triggers") final List<Trigger> triggers,
                 @JsonProperty("condition") final TriggerCondition condition,
                 @JsonProperty("message") final MessageTemplate template,
                 @JsonProperty("channels") final List<String> channels,
                 @JsonProperty("meta") final Meta meta) {
        this.triggers = triggers;
        this.condition = condition;
        this.template = template;
        this.channels = channels;
        this.meta = meta;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public TriggerCondition getCondition() {
        return condition;
    }

    @JsonProperty("message")
    public MessageTemplate getTemplate() {
        return template;
    }

    public List<String> getChannels() {
        return channels;
    }

    public Meta getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
