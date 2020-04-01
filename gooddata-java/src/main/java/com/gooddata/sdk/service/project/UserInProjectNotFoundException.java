/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.project;

import com.gooddata.sdk.common.GoodDataException;

/**
 * User in project is not found
 */
public class UserInProjectNotFoundException extends GoodDataException {

    public UserInProjectNotFoundException(final String message) {
        super(message);
    }

    public UserInProjectNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
