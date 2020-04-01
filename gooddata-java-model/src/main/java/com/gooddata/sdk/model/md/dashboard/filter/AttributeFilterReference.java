/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard.filter;

import static com.gooddata.sdk.common.util.Validate.notEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Reference for attribute filter for ignoring particular filter in {@link com.gooddata.sdk.model.md.dashboard.Kpi}.
 * Is not standalone metadata object - must be part of {@link com.gooddata.sdk.model.md.dashboard.Kpi}.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(AttributeFilterReference.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeFilterReference implements FilterReference {

    private static final long serialVersionUID = -7882622280867466659L;

    static final String NAME = "attributeFilterReference";

    private final String displayFormUri;

    /**
     * Constructor.
     * @param displayFormUri display form URI of filter
     */
    @JsonCreator
    public AttributeFilterReference(@JsonProperty("displayForm") final String displayFormUri) {
        this.displayFormUri = notEmpty(displayFormUri, "displayFormUri");
    }

    /**
     * @return display form URI of the filter
     */
    @JsonProperty("displayForm")
    public String getDisplayFormUri() {
        return displayFormUri;
    }
}
