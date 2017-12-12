/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gooddata.executeafm.ObjQualifier;
import com.gooddata.executeafm.UriObjQualifier;
import com.gooddata.md.visualization.VOPopMeasureDefinition;
import com.gooddata.md.visualization.VOSimpleMeasureDefinition;

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimpleMeasureDefinition.class, name = SimpleMeasureDefinition.NAME),
        @JsonSubTypes.Type(value = PopMeasureDefinition.class, name = PopMeasureDefinition.NAME),
        @JsonSubTypes.Type(value = VOSimpleMeasureDefinition.class, name = VOSimpleMeasureDefinition.NAME),
        @JsonSubTypes.Type(value = VOPopMeasureDefinition.class, name = VOPopMeasureDefinition.NAME),
})
public interface MeasureDefinition {

    /**
     * Returns the definition in the form of uri of {@link com.gooddata.md.Metric}.
     * Default implementation throws {@link UnsupportedOperationException}
     * @return uri of the measure
     */
    @JsonIgnore
    default String getUri() {
        throw new UnsupportedOperationException("This definition has no URI");
    }

    /**
     * Returns the qualifier, qualifying the {@link com.gooddata.md.Metric}.
     * @return qualifier of measure
     */
    @JsonIgnore
    ObjQualifier getObjQualifier();

    /**
     * Copy itself using given uri qualifier
     * @param qualifier qualifier to use for the new filter
     * @return self copy with given qualifier
     */
    MeasureDefinition withObjUriQualifier(UriObjQualifier qualifier);

    /**
     * @return true if this definition represents ad hoc specified measure, false otherwise
     */
    @JsonIgnore
    boolean isAdHoc();
}
