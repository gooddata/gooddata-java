/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.executeafm.afm.Afm;
import com.gooddata.executeafm.afm.LocallyIdentifiable;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.List;
import java.util.Objects;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

/**
 * Header of an attribute.
 */
@JsonRootName(AttributeHeader.NAME)
public class AttributeHeader implements Header, LocallyIdentifiable {

    static final String NAME = "attributeHeader";

    private final String name;
    private final String localIdentifier;
    private final String uri;
    private final String identifier;
    private final AttributeInHeader formOf;

    private List<TotalHeaderItem> totalItems;

    /**
     * Creates new header
     * @deprecated use the constructor with {@link AttributeInHeader}
     *
     * @param name name
     * @param localIdentifier local identifier
     * @param uri uri
     * @param identifier identifier
     */
    @Deprecated
    public AttributeHeader(final String name, final String localIdentifier, final String uri, final String identifier) {
        this.name = notEmpty(name, "name");
        this.localIdentifier = notEmpty(localIdentifier, "localIdentifier");
        this.uri = notEmpty(uri, "uri");
        this.identifier = notEmpty(identifier, "identifier");
        this.formOf = null;
    }

    /**
     * Creates new header
     * @deprecated use the constructor with {@link AttributeInHeader}
     *
     * @param name name
     * @param localIdentifier local identifier
     * @param uri uri
     * @param identifier identifier
     * @param totalHeaderItems total header items
     */
    @Deprecated
    public AttributeHeader(final String name,
                           final String localIdentifier,
                           final String uri,
                           final String identifier,
                           final List<TotalHeaderItem> totalHeaderItems) {
        this.name = notEmpty(name, "name");
        this.localIdentifier = notEmpty(localIdentifier, "localIdentifier");
        this.uri = notEmpty(uri, "uri");
        this.identifier = notEmpty(identifier, "identifier");
        this.formOf = null;
        this.totalItems = totalHeaderItems;
    }

    /**
     * Creates new header
     * @param name name
     * @param localIdentifier local identifier
     * @param uri uri
     * @param identifier identifier
     * @param formOf info about attribute which this header's display form is form of
     */
    public AttributeHeader(final String name, final String localIdentifier, final String uri, final String identifier, final AttributeInHeader formOf) {
        this(name, localIdentifier, uri, identifier, formOf, null);
    }

    /**
     * Creates new header
     * @param name name
     * @param localIdentifier local identifier
     * @param uri uri
     * @param identifier identifier
     * @param formOf info about attribute which this header's display form is form of
     * @param totalHeaderItems total header items
     */
    @JsonCreator
    public AttributeHeader(@JsonProperty("name") final String name,
                           @JsonProperty("localIdentifier") final String localIdentifier,
                           @JsonProperty("uri") final String uri,
                           @JsonProperty("identifier") final String identifier,
                           @JsonProperty("formOf") final AttributeInHeader formOf,
                           @JsonProperty("totalItems") final List<TotalHeaderItem> totalHeaderItems) {
        this.name = notEmpty(name, "name");
        this.localIdentifier = notEmpty(localIdentifier, "localIdentifier");
        this.uri = notEmpty(uri, "uri");
        this.identifier = notEmpty(identifier, "identifier");
        this.formOf = notNull(formOf, "formOf");
        this.totalItems = totalHeaderItems;
    }

    /**
     * Header name, an attribute's display form title, or specified alias.
     * @return header name
     */
    public String getName() {
        return name;
    }

    /**
     * Local identifier referencing header's {@link com.gooddata.executeafm.afm.AttributeItem}
     *  within {@link Afm}
     * @return attribute's local identifier
     */
    @Override
    public String getLocalIdentifier() {
        return localIdentifier;
    }

    /**
     * Uri of attribute's display form
     * @return uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Metadata identifier of attribute's display form
     * @return identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    public AttributeInHeader getFormOf() {
        return formOf;
    }

    /**
     * Totals' headers belonging to the same level as this header.
     * @return lists of totals' header
     */
    public List<TotalHeaderItem> getTotalItems() {
        return totalItems;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AttributeHeader that = (AttributeHeader) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(localIdentifier, that.localIdentifier) &&
                Objects.equals(uri, that.uri) &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(formOf, that.formOf) &&
                Objects.equals(totalItems, that.totalItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, localIdentifier, uri, identifier, formOf, totalItems);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
