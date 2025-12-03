/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import com.gooddata.sdk.model.executeafm.afm.Afm;
import com.gooddata.sdk.model.executeafm.afm.LocallyIdentifiable;
import com.gooddata.sdk.model.executeafm.afm.MeasureItem;

import static com.gooddata.sdk.common.util.Validate.notEmpty;

/**
 * Header of particular measure.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("measureHeaderItem")
public class MeasureHeaderItem implements LocallyIdentifiable {

    private final String name;
    private final String format;
    private final String localIdentifier;
    private String uri;
    private String identifier;

    public MeasureHeaderItem(final String name, final String format, final String localIdentifier) {
        this.name = name;
        this.format = format;
        this.localIdentifier = localIdentifier;
    }

    @JsonCreator
    public MeasureHeaderItem(@JsonProperty("name") final String name,
                             @JsonProperty("format") final String format,
                             @JsonProperty("localIdentifier") final String localIdentifier,
                             @JsonProperty("uri") final String uri,
                             @JsonProperty("identifier") final String identifier) {
        this.name = notEmpty(name, "name");
        this.format = notEmpty(format, "format");
        this.localIdentifier = notEmpty(localIdentifier, "localIdentifier");
        this.uri = uri;
        this.identifier = identifier;
    }

    /**
     * Header name, can be measure title, or specified alias
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return Measure format
     */
    public String getFormat() {
        return format;
    }

    /**
     * Local identifier, referencing the {@link MeasureItem}
     * in {@link Afm}
     *
     * @return local identifier
     */
    @Override
    public String getLocalIdentifier() {
        return localIdentifier;
    }

    /**
     * @return Measure uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Set measure uri
     *
     * @param uri measure uri
     */
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * @return Measure metadata identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Set measure metadata identifier
     *
     * @param identifier
     */
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
