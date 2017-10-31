/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.gooddata.executeafm.ObjQualifier;

import static com.gooddata.util.Validate.notNull;

/**
 * Represents filter by attribute.
 */
public abstract class AttributeFilter implements FilterItem {
    private final ObjQualifier displayForm;

    /**
     * Creates new filter
     * @param displayForm qualifier of attribute's display form to be filtered
     */
    AttributeFilter(final ObjQualifier displayForm) {
        this.displayForm = notNull(displayForm, "displayForm");
    }

    /**
     * @return filtered attribute's display form qualifier
     */
    public ObjQualifier getDisplayForm() {
        return displayForm;
    }

    @Override
    public ObjQualifier getObjQualifier() {
        return getDisplayForm();
    }
}
