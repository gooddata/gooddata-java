/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.projecttemplate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Wrapper for list of project templates.
 * Deserialization only.
 *
 * @deprecated The project templates are deprecated and stopped working on May 15, 2019.
 * See https://support.gooddata.com/hc/en-us/articles/360016126334-April-4-2019
 * Deprecated since version 3.0.1. Will be removed in one of future versions.
 */
@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
public class Templates {

    public static final String URI = "/projectTemplates";
    private final List<Template> templates;

    @JsonCreator
    Templates(@JsonProperty("projectTemplates") List<Template> templates) {
        this.templates = templates;
    }

    public List<Template> getTemplates() {
        return templates;
    }
}
