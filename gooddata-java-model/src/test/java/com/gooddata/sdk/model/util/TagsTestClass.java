/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Set;

class TagsTestClass {

    private final Set<String> tags;

    @JsonCreator
    TagsTestClass(@JsonProperty("tags") @JsonDeserialize(using = TagsDeserializer.class) final Set<String> tags) {
        this.tags = tags;
    }

    @JsonSerialize(using = TagsSerializer.class)
    public Set<String> getTags() {
        return tags;
    }
}
