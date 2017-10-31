/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.gooddata.executeafm.ObjQualifier;
import com.gooddata.executeafm.UriObjQualifier;

/**
 * Covers all the filters which can be used within AFM
 */
@JsonSubTypes({
        @JsonSubTypes.Type(value = PositiveAttributeFilter.class, name = PositiveAttributeFilter.NAME),
        @JsonSubTypes.Type(value = NegativeAttributeFilter.class, name = NegativeAttributeFilter.NAME),
        @JsonSubTypes.Type(value = AbsoluteDateFilter.class, name = AbsoluteDateFilter.NAME),
        @JsonSubTypes.Type(value = RelativeDateFilter.class, name = RelativeDateFilter.NAME)
})
public interface FilterItem extends CompatibilityFilter {


    /**
     * Get qualifier of {@link com.gooddata.md.Obj} to which the filter relates.
     * @return filtered object qualifier
     */
    @JsonIgnore
    ObjQualifier getObjQualifier();

    /**
     * Copy itself using given uri qualifier
     * @param qualifier qualifier to use for the new filter
     * @return self copy with given qualifier
     */
    FilterItem withObjUriQualifier(UriObjQualifier qualifier);
}
