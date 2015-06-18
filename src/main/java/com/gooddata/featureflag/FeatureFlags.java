/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.featureflag;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.web.util.UriTemplate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.gooddata.util.Validate.notEmpty;
import static org.springframework.util.Assert.notNull;

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("featureFlags")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureFlags implements Iterable<FeatureFlag> {

    public static final String AGGREGATED_FEATURE_FLAGS_URI = "/gdc/internal/projects/{projectId}/featureFlags";
    public static final UriTemplate AGGREGATED_FEATURE_FLAGS_TEMPLATE = new UriTemplate(AGGREGATED_FEATURE_FLAGS_URI);

    private final List<FeatureFlag> featureFlags = new LinkedList<>();

    /* protected helper method for JSON deserialization */
    @JsonAnySetter
    protected void addFlag(final String name, final boolean enabled) {
        notNull(name);
        featureFlags.add(new FeatureFlag(name, enabled));
    }

    /**
     * @deprecated use {@link #isEnabled(String)} method or {@link Iterable} feature of this class
     */
    @Deprecated
    public List<FeatureFlag> getFeatureFlags() {
        return featureFlags;
    }

    /**
     * {@inheritDoc Iterable#iterator}
     */
    @Override
    public Iterator<FeatureFlag> iterator() {
        return featureFlags.iterator();
    }

    /**
     * Returns true if the feature flag with given name exists and is enabled, false otherwise.
     *
     * @param flagName the name of feature flag
     * @return true if the feature flag with given name exists and is enabled, false otherwise
     */
    public boolean isEnabled(final String flagName) {
        notEmpty(flagName, "flagName");
        for (final FeatureFlag flag : featureFlags) {
            if (flagName.equalsIgnoreCase(flag.getName())) {
                return flag.isEnabled();
            }
        }
        return false;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final FeatureFlags that = (FeatureFlags) o;

        return !(featureFlags != null ? !featureFlags.equals(that.featureFlags) : that.featureFlags != null);

    }

    @Override
    public int hashCode() {
        return featureFlags != null ? featureFlags.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FeatureFlags{" + featureFlags + "}";
    }
}
