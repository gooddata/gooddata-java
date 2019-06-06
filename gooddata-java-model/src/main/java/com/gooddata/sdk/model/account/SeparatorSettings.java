/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;

/**
 * Default thousand and decimal separator settings configured for a profile.
 * Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("separators")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeparatorSettings implements Serializable {

    private static final long serialVersionUID = 446547615105910660L;

    public static final String URI = "/gdc/account/profile/{id}/settings/separators";

    private final String thousand;
    private final String decimal;
    private final Links links;

    @JsonCreator
    private SeparatorSettings(
            @JsonProperty("thousand") final String thousand,
            @JsonProperty("decimal") final String decimal,
            @JsonProperty("links") final Links links) {
        this.thousand = thousand;
        this.decimal = decimal;
        this.links = links;
    }

    public String getThousand() {
        return thousand;
    }

    public String getDecimal() {
        return decimal;
    }

    public String getSelfLink() {
        return links.self;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.toString(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Links implements Serializable{
        private final String self;

        @JsonCreator
        private Links(@JsonProperty("self") final String self) {
            this.self = self;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.toString(this);
        }
    }
}
