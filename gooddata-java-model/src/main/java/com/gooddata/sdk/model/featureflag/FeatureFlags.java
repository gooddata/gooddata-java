/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.featureflag;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.*;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;
import static java.util.stream.Collectors.toMap;

@Deprecated
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("featureFlags")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureFlags implements Iterable<FeatureFlag> {

    public static final String AGGREGATED_FEATURE_FLAGS_URI = "/gdc/internal/projects/{projectId}/featureFlags";

    private final List<FeatureFlag> featureFlags = new LinkedList<>();

    /**
     * Adds the feature flag of given name and given value.
     *
     * @param name    feature flag name
     * @param enabled feature flag value (enabled / disabled)
     */
    @JsonAnySetter
    public void addFlag(final String name, final boolean enabled) {
        notNull(name, "name");
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
     *
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
        return featureFlags.equals(that.featureFlags);
    }

    @Override
    public int hashCode() {
        return featureFlags.hashCode();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
