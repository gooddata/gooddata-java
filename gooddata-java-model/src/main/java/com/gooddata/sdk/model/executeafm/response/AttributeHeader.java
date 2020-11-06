/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.sdk.model.executeafm.afm.Afm;
import com.gooddata.sdk.model.executeafm.afm.LocallyIdentifiable;
import com.gooddata.sdk.model.executeafm.afm.AttributeItem;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.List;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

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
    private final String type;
    private final AttributeInHeader formOf;

    private List<TotalHeaderItem> totalItems;

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
    public AttributeHeader(@JsonProperty("name") final String name,
                           @JsonProperty("localIdentifier") final String localIdentifier,
                           @JsonProperty("uri") final String uri,
                           @JsonProperty("identifier") final String identifier,
                           @JsonProperty("formOf") final AttributeInHeader formOf,
                           @JsonProperty("totalItems") final List<TotalHeaderItem> totalHeaderItems) {
        this(name, localIdentifier, uri, identifier, null, formOf, totalHeaderItems);
    }

    /**
     * Creates new header
     * @param name name
     * @param localIdentifier local identifier
     * @param uri uri
     * @param identifier identifier
     * @param type type
     * @param formOf info about attribute which this header's display form is form of
     * @param totalHeaderItems total header items
     */
    @JsonCreator
    public AttributeHeader(@JsonProperty("name") final String name,
                           @JsonProperty("localIdentifier") final String localIdentifier,
                           @JsonProperty("uri") final String uri,
                           @JsonProperty("identifier") final String identifier,
                           @JsonProperty("type") final String type,
                           @JsonProperty("formOf") final AttributeInHeader formOf,
                           @JsonProperty("totalItems") final List<TotalHeaderItem> totalHeaderItems) {
        this.name = notEmpty(name, "name");
        this.localIdentifier = notEmpty(localIdentifier, "localIdentifier");
        this.uri = notEmpty(uri, "uri");
        this.identifier = notEmpty(identifier, "identifier");
        this.type = type;
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
     * Local identifier referencing header's {@link AttributeItem}
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

    /**
     * Metadata type of attribute's display form
     * @return type
     */
    public String getType() {
        return type;
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
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
