/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.notification;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.account.Account;
import com.gooddata.md.Meta;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriTemplate;

/**
 * Notification channel
 */
@JsonTypeName("channelConfiguration")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {

    public static final String URI = Account.URI + "/channelConfigurations";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(URI);

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
