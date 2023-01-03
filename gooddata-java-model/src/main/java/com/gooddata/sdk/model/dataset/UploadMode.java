/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import java.util.HashMap;
import java.util.Map;

/**
 * Upload modes enum.
 */
public enum UploadMode {
    /**
     * Dataset input for loading in full mode (reload from scratch)
     */
    FULL("FULL"),
    /**
     * Dataset input for loading data incrementally
     */
    INCREMENTAL("INCREMENTAL"),
    /**
     * Dataset input for loading data by calculating based on its attributes.
     * Example: last sync, max timestamp, having timestamp field,...
     */
    DEFAULT("DEFAULT"),
    /**
     * Dataset input for deleting
     */
    DELETE_CENTER("DELETE-CENTER");

    private static final Map<String, UploadMode> lookup = new HashMap<>();

    static {
        for (UploadMode m : UploadMode.values()) {
            lookup.put(m.modeStr, m);
        }
    }

    private final String modeStr;

    /**
     * @param modeStr string representation of the upload mode
     */
    UploadMode(final String modeStr) {
        this.modeStr = modeStr;
    }

    /**
     * Lookup method.
     *
     * @param mode a string value passed in the constructor
     * @return UploadMode enum instance according to the given mode string
     */
    public static UploadMode get(String mode) {
        return lookup.get(mode);
    }

    @Override
    public String toString() {
        return this.modeStr;
    }
}