/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Set;

public class TagsTestClass {

    private final Set<String> tags;

    @JsonCreator
    public TagsTestClass(
            @JsonProperty("tags") @JsonDeserialize(using = TagsDeserializer.class) final Set<String> tags) {
        this.tags = tags;
    }

    @JsonSerialize(using = TagsSerializer.class)
    public Set<String> getTags() {
        return tags;
    }
}
