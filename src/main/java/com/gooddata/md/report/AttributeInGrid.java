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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

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
