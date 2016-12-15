/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.featureflag;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.web.util.UriTemplate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.gooddata.util.Validate.notEmpty;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notNull;

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("featureFlags")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureFlags implements Iterable<FeatureFlag> {

    public static final String AGGREGATED_FEATURE_FLAGS_URI = "/gdc/internal/projects/{projectId}/featureFlags";
    public static final UriTemplate AGGREGATED_FEATURE_FLAGS_TEMPLATE = new UriTemplate(AGGREGATED_FEATURE_FLAGS_URI);

    private final List<FeatureFlag> featureFlags = new LinkedList<>();

    /**
     * Adds the feature flag of given name and given value.
     *
     * @param name feature flag name
     * @param enabled feature flag value (enabled / disabled)
     */
    @JsonAnySetter
    public void addFlag(final String name, final boolean enabled) {
        notNull(name);
        featureFlags.add(new FeatureFlag(name, enabled));
    }

    /**
     * Removes flag of given name.
     *
     * @param flagName name of the flag to remove
     */
    public void removeFlag(final String flagName) {
        findFlag(flagName).ifPresent(featureFlags::remove);
    }

    /**
     * Converts feature flags to map where flags' names are the keys and values are flags' enabled property
     * @return feature flags as map
     */
    @JsonAnyGetter
    private Map<String, Boolean> asMap() {
        return featureFlags.stream().collect(toMap(FeatureFlag::getName, FeatureFlag::isEnabled));
    }

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
        return findFlag(flagName).map(FeatureFlag::isEnabled).orElse(false);
    }

    private Optional<FeatureFlag> findFlag(final String flagName) {
        return featureFlags.stream().filter(f -> flagName.equalsIgnoreCase(f.getName())).findAny();
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
