/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.gooddata.sdk.model.executeafm.ObjQualifier;

import java.io.Serializable;
import java.util.Objects;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Represents filter by attribute.
 */
public abstract class AttributeFilter implements FilterItem, Serializable {

    private static final long serialVersionUID = 1125099080537899747L;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeFilter that = (AttributeFilter) o;
        return Objects.equals(displayForm, that.displayForm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayForm);
    }

}
