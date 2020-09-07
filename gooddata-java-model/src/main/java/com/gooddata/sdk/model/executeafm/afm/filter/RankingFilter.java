/*
 * Copyright (C) 2007-2020, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import com.gooddata.sdk.common.util.Validate;
import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier;
import com.gooddata.sdk.model.executeafm.ObjQualifier;
import com.gooddata.sdk.model.executeafm.Qualifier;
import com.gooddata.sdk.model.executeafm.afm.ObjQualifierConverter;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static java.lang.String.format;

/**
 * Represents a ranking filter applied on an insight.
 */
@JsonRootName(RankingFilter.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RankingFilter implements ExtendedFilter, CompatibilityFilter, Serializable {

    public static final String NAME = "rankingFilter";

    private static final long serialVersionUID = 2642298346540031612L;

    private final List<Qualifier> measures;
    private final List<Qualifier> attributes;
    private final String operator;
    private final Integer value;

    /**
     * Creates a new {@link RankingFilter} instance.
     *
     * @param measures measures on which is the ranking applied. Must not be null.
     * @param attributes attributes that define ranking granularity. Optional, can be null.
     * @param operator operator that defines the type of ranking.
     * @param value number of requested ranked records.
     *
     * @throws NullPointerException thrown when required parameter is not provided.
     */
    @JsonCreator
    public RankingFilter(
            @JsonProperty("measures") final List<Qualifier> measures,
            @JsonProperty("attributes") final List<Qualifier> attributes,
            @JsonProperty("operator") final String operator,
            @JsonProperty("value") final Integer value) {
        this.measures = notNull(measures, "measures must not be null!");
        this.attributes = attributes;
        this.operator = notNull(operator, "operator must not be null!");
        this.value = notNull(value, "value must not be null!");
    }

    public RankingFilter(
            final List<Qualifier> measures,
            final List<Qualifier> attributes,
            final RankingFilterOperator operator,
            final Integer value) {
        this(measures, attributes, notNull(operator, "operator must not be null!").name(), value);
    }

    /**
     * Returns all the qualifiers used by the ranking filter.
     * <p>
     * This information comes handy if it is necessary, for example, to convert the ranking filter to use just
     * the URI object qualifiers instead of the identifier object qualifiers. It can be used to gather these
     * for a conversion service.
     *
     * @return all the qualifiers the ranking filter uses
     */
    @JsonIgnore
    public Collection<ObjQualifier> getObjQualifiers() {
        return Stream.concat(
                this.measures.stream(),
                this.attributes == null ? Stream.empty() : this.attributes.stream()
        )
                .filter(ObjQualifier.class::isInstance)
                .map(ObjQualifier.class::cast)
                .collect(Collectors.toSet());
    }

    /**
     * Copy itself using the given object qualifier converter in case when {@link IdentifierObjQualifier} instances are used in the object otherwise the
     * original object is returned.
     * <p>
     * The provided converter must be able to handle the conversion for the qualifiers that are of the {@link IdentifierObjQualifier} type that are used by
     * this object or its encapsulated child objects.
     *
     * @param objQualifierConverter The function that converts identifier qualifiers to the matching URI qualifiers. In case when the object uses the
     *         identifier qualifiers, it
     *         will return a new copy of itself or its encapsulated objects that used URI qualifiers, otherwise the original object is returned.
     *         The parameter must not be null.
     *
     * @return copy of itself with replaced qualifiers in case when some {@link IdentifierObjQualifier} were used, otherwise original object is returned.
     *
     * @throws IllegalArgumentException The exception is thrown when conversion for the identifier qualifier used by this ranking filter could not be
     *         made by the provided
     *         converter or when provided converter is null.
     */
    public RankingFilter withObjUriQualifiers(final ObjQualifierConverter objQualifierConverter) {
        notNull(objQualifierConverter, "objQualifierConverter");

        return new RankingFilter(
                translateIdentifierQualifiers(this.measures, objQualifierConverter),
                attributes == null ? null : translateIdentifierQualifiers(this.attributes, objQualifierConverter),
                operator,
                value
        );
    }

    private List<Qualifier> translateIdentifierQualifiers(final List<Qualifier> qualifiers, final ObjQualifierConverter objQualifierConverter) {
        return qualifiers.stream()
                .map(qualifier -> translateIdentifierQualifier(qualifier, objQualifierConverter))
                .collect(Collectors.toList());
    }

    private Qualifier translateIdentifierQualifier(final Qualifier qualifier, final ObjQualifierConverter objQualifierConverter) {
        if (qualifier instanceof IdentifierObjQualifier) {
            final IdentifierObjQualifier identifierQualifierToConvert = (IdentifierObjQualifier) qualifier;
            return objQualifierConverter.convertToUriQualifier(identifierQualifierToConvert)
                    .orElseThrow(() -> buildExceptionForFailedConversion(identifierQualifierToConvert));
        }
        return qualifier;
    }

    private static IllegalArgumentException buildExceptionForFailedConversion(final IdentifierObjQualifier qualifierFailedToConvert) {
        return new IllegalArgumentException(format("Supplied converter does not provide conversion for '%s'!", qualifierFailedToConvert));
    }

    /**
     * @return measures on which is the ranking applied
     */
    public List<Qualifier> getMeasures() {
        return measures;
    }

    /**
     * @return granularity of the ranking
     */
    public List<Qualifier> getAttributes() {
        return attributes;
    }

    /**
     * Get operator as an enum constant for easier programmatic access.
     * @return ranking operator constant
     */
    @JsonIgnore
    public RankingFilterOperator getOperator() {
        return RankingFilterOperator.of(this.operator);
    }

    /**
     * Get operator as a string representation of the {@link RankingFilterOperator} enum constant as it was parsed from the JSON.
     * @return string operator provided at the time of the filter instance creation
     */
    @JsonProperty("operator")
    public String getOperatorAsString() {
        return operator;
    }

    /**
     * @return number of ranked records
     */
    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RankingFilter that = (RankingFilter) o;
        return Objects.equals(measures, that.measures) &&
                Objects.equals(attributes, that.attributes) &&
                Objects.equals(operator, that.operator) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(measures, attributes, operator, value);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
