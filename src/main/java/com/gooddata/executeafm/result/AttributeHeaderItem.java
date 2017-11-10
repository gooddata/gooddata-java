/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import static com.gooddata.executeafm.result.AttributeHeaderItem.NAME;

/**
 * Header item for attribute
 */
@JsonRootName(NAME)
public class AttributeHeaderItem extends ResultHeaderItem {

    static final String NAME = "attributeHeaderItem";

    private final String uri;

    /**
     * Creates new header item
     * @param name name of item (usually attribute element title)
     * @param uri uri of item (usually attribute element uri)
     */
    @JsonCreator
    public AttributeHeaderItem(@JsonProperty("name") final String name, @JsonProperty("uri") final String uri) {
        super(name);
        this.uri = uri;
    }

    /**
     * @return item uri
     */
    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
