/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UseManyEntries {

    private final String uri;

    private final Collection<Entry> entries;

    @JsonCreator
    UseManyEntries(@JsonProperty("uri") final String uri,
                   @JsonProperty("entries") final Collection<Entry> entries) {
        this.uri = uri;
        this.entries = entries;
    }

    public String getUri() {
        return uri;
    }

    public Collection<Entry> getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
