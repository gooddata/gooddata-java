/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import static com.gooddata.util.Validate.notEmpty;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Objects;

/**
 * Coupa connector instance.
 */
@JsonTypeName("coupaInstance")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoupaInstance {

    private final String name;
    private final String apiUrl;
    private final String apiKey;

    /**
     * Constructor.
     *
     * @param name instance name
     * @param apiUrl API URL
     * @param apiKey API key for this instance (can be hidden = {@code null})
     */
    @JsonCreator
    public CoupaInstance(
            @JsonProperty("name") String name,
            @JsonProperty("apiUrl") String apiUrl,
            @JsonProperty("apiKey") String apiKey) {
        notEmpty(name, "name");
        notEmpty(apiUrl, "apiUrl");

        this.name = name;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public String getName() {
        return name;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CoupaInstance))
            return false;
        CoupaInstance that = (CoupaInstance) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(apiUrl, that.apiUrl) &&
                Objects.equals(apiKey, that.apiKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, apiUrl, apiKey);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
