/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.md.Attribute;
import com.gooddata.md.DisplayForm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import static com.gooddata.util.Validate.notNull;

/**
 * Attribute in Grid
 */
@JsonTypeName("attribute")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeInGrid implements GridElement {

    private final String uri;
    private final String alias;
    private Collection<Collection<String>> totals;

    @JsonCreator
    AttributeInGrid(@JsonProperty("uri") String uri, @JsonProperty("totals") Collection<Collection<String>> totals,
                    @JsonProperty("alias") String alias) {
        this.uri = uri;
        this.alias = alias;
        this.totals = totals;
    }

    public AttributeInGrid(String uri) {
        this(uri, new ArrayList<Collection<String>>(), null);
    }

    public AttributeInGrid(String uri, String alias) {
        this(uri, new ArrayList<Collection<String>>(), alias);
    }

    /**
     * Creates new AttributeInGrid using given DisplayForm's uri and it's title as alias.
     * @param displayForm displayForm to create AttributeInGrid from
     */
    public AttributeInGrid(final DisplayForm displayForm) {
        this(notNull(notNull(displayForm, "displayForm").getUri(), "uri"), notNull(displayForm, "displayForm").getTitle());
    }

    /**
     * Creates new AttributeInGrid using given Attribute's default DisplayForm's uri and it's title as alias.
     * @param attribute attribute to create AttributeInGrid from
     */
    public AttributeInGrid(final Attribute attribute) {
        this(notNull(attribute, "attribute").getDefaultDisplayForm());
    }

    public Collection<Collection<String>> getTotals() {
        final LinkedList<Collection<String>> result = new LinkedList<>();
        for (final Collection<String> t : totals) {
            result.add(t);
        }
        return result;
    }

    public String getUri() {
        return uri;
    }

    public String getAlias() {
        return alias;
    }
}
