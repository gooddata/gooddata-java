/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.featureflag;

import com.fasterxml.jackson.annotation.*;
import org.springframework.web.util.UriTemplate;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNullState;

/**
 * Project feature flag is a boolean flag used for enabling / disabling some specific feature of GoodData platform
 * on per project basis.
 */
@JsonTypeName("featureFlag")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectFeatureFlag {

    public static final String PROJECT_FEATURE_FLAG_URI = ProjectFeatureFlags.PROJECT_FEATURE_FLAGS_URI + "/{featureFlag}";
    public static final UriTemplate PROJECT_FEATURE_FLAG_TEMPLATE = new UriTemplate(PROJECT_FEATURE_FLAG_URI);

    private final String name;
    private boolean enabled;

    @JsonIgnore
    private final Links links;

    /**
     * Creates new project feature flag which is by default enabled (true).
     *
     * @param name unique name of feature flag
     */
    public ProjectFeatureFlag(String name) {
        this(notEmpty(name, "name"), true, null);
    }

    /**
     * Creates new project feature flag with given value.
     *
     * @param name unique name of feature flag
     * @param enabled true (flag enabled) or false (flag disabled)
     */
    public ProjectFeatureFlag(String name, boolean enabled) {
        this(notEmpty(name, "name"), enabled, null);
    }

    @JsonCreator
    ProjectFeatureFlag(@JsonProperty("key") String name,
                       @JsonProperty("value") boolean enabled,
                       @JsonProperty("links") Links links) {
        this.name = name;
        this.enabled = enabled;
        this.links = links;
    }

    @JsonProperty("key")
    public String getName() {
        return name;
    }

    @JsonProperty("value")
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").getSelf();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ProjectFeatureFlag that = (ProjectFeatureFlag) o;

        if (enabled != that.enabled) {
            return false;
        }
        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProjectFeatureFlag{" +
                "name='" + name + '\'' +
                ", enabled=" + enabled +
                ", links=" + links +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Links {
        private final String self;

        @JsonCreator
        public Links(@JsonProperty("self") String self) {
            this.self = self;
        }

        public String getSelf() {
            return self;
        }

        @Override
        public String toString() {
            return "Links{" +
                    "self='" + self + '\'' +
                    '}';
        }
    }
}
