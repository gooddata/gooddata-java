/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.notification;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.model.md.Meta;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Notification channel
 */
@JsonTypeName("channelConfiguration")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {

    public static final String URI = Account.URI + "/channelConfigurations";

    private final Configuration configuration;
    private final Meta meta;

    public Channel(final Configuration configuration,
                   final String title) {
        this(
                notNull(configuration, "configuration"),
                new Meta(
                        notEmpty(title, "title")
                )
        );
    }

    @JsonCreator
    private Channel(@JsonProperty("configuration") final Configuration configuration,
                    @JsonProperty("meta") final Meta meta) {
        this.configuration = configuration;
        this.meta = meta;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Meta getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
