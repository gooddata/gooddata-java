/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.auditevent;

import com.gooddata.GoodDataException;

public class AuditEventsForbiddenException extends GoodDataException {

    public AuditEventsForbiddenException(final Throwable cause) {
        super("Unable to list audit events", cause);
    }
}
