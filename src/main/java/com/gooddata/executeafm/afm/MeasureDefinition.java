/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gooddata.executeafm.IdentifierObjQualifier;
import com.gooddata.executeafm.ObjQualifier;
import com.gooddata.executeafm.UriObjQualifier;
import com.gooddata.md.visualization.VOPopMeasureDefinition;
import com.gooddata.md.visualization.VOSimpleMeasureDefinition;

import java.util.Collection;

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimpleMeasureDefinition.class, name = SimpleMeasureDefinition.NAME),
        @JsonSubTypes.Type(value = PopMeasureDefinition.class, name = PopMeasureDefinition.NAME),
        @JsonSubTypes.Type(value = VOSimpleMeasureDefinition.class, name = VOSimpleMeasureDefinition.NAME),
        @JsonSubTypes.Type(value = VOPopMeasureDefinition.class, name = VOPopMeasureDefinition.NAME),
        @JsonSubTypes.Type(value = OverPeriodMeasureDefinition.class, name = OverPeriodMeasureDefinition.NAME),
        @JsonSubTypes.Type(value = PreviousPeriodMeasureDefinition.class, name = PreviousPeriodMeasureDefinition.NAME),
        @JsonSubTypes.Type(value = ArithmeticMeasureDefinition.class, name = ArithmeticMeasureDefinition.NAME)
})
public interface MeasureDefinition {

    /**
     * Returns the definition in the form of uri of {@link com.gooddata.md.Metric}.
     * Default implementation throws {@link UnsupportedOperationException}
     *
     * @return uri of the measure
     */
    @JsonIgnore
    default String getUri() {
        throw new UnsupportedOperationException("This definition has no URI");
    }

    /**
     * Returns the qualifier, qualifying the {@link com.gooddata.md.Metric}.
     *
     * @return qualifier of measure
     *
     * @throws UnsupportedOperationException
     *         The exception is thrown when the method is not supported by the implementation.
     * @deprecated Use {@link #withObjUriQualifiers(ObjQualifierConverter)} instead as this method is not supported by all the existing implementations.
     */
    @JsonIgnore
    @Deprecated
    ObjQualifier getObjQualifier();

    /**
     * Returns all the qualifiers used by the measure definition and its encapsulated objects.
     * <p>
     * This information comes handy if it is necessary, for example, to convert the measure definition to use just the URI object qualifiers instead of the
     * identifier object qualifiers. It can be used to gather these for a conversion service.
     *
     * @return all the qualifiers the measure definition uses, even in its encapsulated objects (apart from the measure filters)
     */
    @JsonIgnore
    Collection<ObjQualifier> getObjQualifiers();

    /**
     * Copy itself using given URI qualifier.
     *
     * @param qualifier
     *         The qualifier to use by the new object.
     *
     * @return self copy with given qualifier
     *
     * @throws UnsupportedOperationException
     *         The exception is thrown when the method is not supported by the implementation.
     * @deprecated Use {@link #withObjUriQualifiers(ObjQualifierConverter)} instead as this method is not supported by all the existing implementations.
     */
    @Deprecated
    MeasureDefinition withObjUriQualifier(UriObjQualifier qualifier);

    /**
     * Copy itself using the given object qualifier converter in case when {@link IdentifierObjQualifier} instances are used in the object otherwise the
     * original object is returned.
     * <p>
     * The provided converter must be able to handle the conversion for the qualifiers that are of the {@link IdentifierObjQualifier} type that are used by
     * this object or its encapsulated child objects.
     *
     * @param objQualifierConverter
     *         The function that converts identifier qualifiers to the matching URI qualifiers. In case when the object uses the identifier qualifiers, it
     *         will return a new copy of itself or its encapsulated objects that used URI qualifiers, otherwise the original object is returned.
     *         The parameter must not be null.
     *
     * @return copy of itself with replaced qualifiers in case when some {@link IdentifierObjQualifier} were used, otherwise original object is returned.
     *
     * @throws IllegalArgumentException
     *         The exception is thrown when conversion for the identifier qualifier used by this measure definition could not be made by the provided
     *         converter or when provided converter is null.
     */
    MeasureDefinition withObjUriQualifiers(ObjQualifierConverter objQualifierConverter);

    /**
     * @return true if this definition represents ad hoc specified measure, false otherwise
     */
    @JsonIgnore
    boolean isAdHoc();
}
