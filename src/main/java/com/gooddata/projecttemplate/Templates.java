/**
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.projecttemplate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Wrapper for list of project templates.
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Templates {

    static final String URI = "/projectTemplates";
    private final List<Template> templates;

    @JsonCreator
    Templates(@JsonProperty("projectTemplates") List<Template> templates) {
        this.templates = templates;
    }

    List<Template> getTemplates() {
        return templates;
    }
}
