/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm;

/**
 * Marker interface for all locally identifiable objects having local identifier in AFM
 */
public interface LocallyIdentifiable {
    /**
     * @return value of local identifier, unique within {@link Afm}
     */
    String getLocalIdentifier();
}
