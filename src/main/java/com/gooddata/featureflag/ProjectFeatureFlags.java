/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.featureflag;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.project.Project;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriTemplate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.gooddata.util.Validate.notEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("featureFlags")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectFeatureFlags implements Iterable<ProjectFeatureFlag> {

    public static final String PROJECT_FEATURE_FLAGS_URI = Project.URI + "/projectFeatureFlags";
    public static final UriTemplate PROJECT_FEATURE_FLAGS_TEMPLATE = new UriTemplate(PROJECT_FEATURE_FLAGS_URI);

    @JsonProperty("items")
    private final List<ProjectFeatureFlag> items = new LinkedList<>();

    @JsonCreator
    ProjectFeatureFlags(@JsonProperty("items") List<ProjectFeatureFlag> items) {
        if (!isEmpty(items)) {
            this.items.addAll(items);
        }
    }

    @Override
    public Iterator<ProjectFeatureFlag> iterator() {
        return items.iterator();
    }

    /**
     * Returns true if the project feature flag with given name exists and is enabled, false otherwise.
     *
     * @param flagName the name of project feature flag
     * @return true if the project feature flag with given name exists and is enabled, false otherwise
     */
    public boolean isEnabled(final String flagName) {
        notEmpty(flagName, "flagName");
        for (final ProjectFeatureFlag flag : items) {
            if (flagName.equalsIgnoreCase(flag.getName())) {
                return flag.isEnabled();
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
