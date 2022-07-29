/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.util;

/**
 * Utilities for URI.
 */
public abstract class UriHelper {

    /**
     * Parses the last part of the URI (substring after last '/' sign).
     * @param uri uri to get the last part form
     * @return last part of the uri
     */
    public static String getLastUriPart(final String uri) {
        return uri == null ? null : uri.substring(uri.lastIndexOf("/") + 1);
    }
}
