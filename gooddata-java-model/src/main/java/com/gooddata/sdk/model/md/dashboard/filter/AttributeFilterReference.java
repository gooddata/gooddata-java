/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import static com.gooddata.sdk.common.util.Validate.notEmpty;

/**
 * Reference for attribute filter for ignoring particular filter in {@link com.gooddata.sdk.model.md.dashboard.Kpi}.
 * Is not standalone metadata object - must be part of {@link com.gooddata.sdk.model.md.dashboard.Kpi}.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(AttributeFilterReference.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeFilterReference implements FilterReference {

    static final String NAME = "attributeFilterReference";
    private static final long serialVersionUID = -7882622280867466659L;
    private final String displayFormUri;

    /**
     * Constructor.
     *
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
