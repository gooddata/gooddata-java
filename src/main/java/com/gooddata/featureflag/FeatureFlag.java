/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.featureflag;

import static com.gooddata.util.Validate.notNull;

/**
 * Feature flag is a boolean flag used for enabling / disabling some specific feature of GoodData platform.
 * It can be used in various scopes (per project, per project group, per user, global etc.).
 */
public class FeatureFlag {

    private final String name;
    private boolean enabled;

    public FeatureFlag(final String name, final boolean enabled) {
        this.name = notNull(name, "name cannot be null");
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final FeatureFlag that = (FeatureFlag) o;

        if (enabled != that.enabled) return false;
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
        return "{name='" + name + '\'' + ", enabled=" + enabled + "}";
    }
}
