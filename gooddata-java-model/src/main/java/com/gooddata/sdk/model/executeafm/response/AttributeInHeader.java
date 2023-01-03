/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import static com.gooddata.sdk.common.util.Validate.notEmpty;

/**
 * Represents attribute in {@link AttributeHeader}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeInHeader {
    private final String name;
    private final String uri;
    private final String identifier;

    /**
     * Creates new instance
     * @param name attribute's title
     * @param uri attribute's uri
     * @param identifier attribute's identifier
     */
    @JsonCreator
    public AttributeInHeader(@JsonProperty("name") final String name,
                             @JsonProperty("uri") final String uri,
                             @JsonProperty("identifier") final String identifier) {
        this.name = notEmpty(name, "name");
        this.uri = notEmpty(uri, "uri");
        this.identifier = notEmpty(identifier, "identifier");
    }

    /**
     * @return attribute's title
     */
    public String getName() {
        return name;
    }

    /**
     * @return attribute's uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @return attribute's identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttributeInHeader attributeInHeader = (AttributeInHeader) o;

        if (name != null ? !name.equals(attributeInHeader.name) : attributeInHeader.name != null) return false;
        if (uri != null ? !uri.equals(attributeInHeader.uri) : attributeInHeader.uri != null) return false;
        return identifier != null ? identifier.equals(attributeInHeader.identifier) : attributeInHeader.identifier == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
