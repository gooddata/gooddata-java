/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

/**
 * Marker interface for all locally identifiable objects having local identifier in AFM
 */
public interface LocallyIdentifiable {
    /**
     * @return value of local identifier, unique within {@link ObjectAfm}
     */
    String getLocalIdentifier();
}
