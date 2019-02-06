/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;

import static com.gooddata.util.Validate.notEmpty;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.util.GoodDataToStringBuilder;

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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CoupaInstance that = (CoupaInstance) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (apiUrl != null ? !apiUrl.equals(that.apiUrl) : that.apiUrl != null) return false;
        return apiKey != null ? apiKey.equals(that.apiKey) : that.apiKey == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (apiUrl != null ? apiUrl.hashCode() : 0);
        result = 31 * result + (apiKey != null ? apiKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
