/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.auditevent;

import com.gooddata.sdk.common.GoodDataException;

public class AuditEventsForbiddenException extends GoodDataException {

    public AuditEventsForbiddenException(final Throwable cause) {
        super("Unable to list audit events", cause);
    }
}
