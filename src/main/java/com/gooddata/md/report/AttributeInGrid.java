/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Attribute in Grid
 */
@JsonTypeName("attribute")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AttributeInGrid extends GridElement {

    private Collection<Collection<String>> totals;

    @JsonCreator
    AttributeInGrid(@JsonProperty("uri") String uri, @JsonProperty("totals") Collection<Collection<String>> totals,
                    @JsonProperty("alias") String alias) {
        super(uri, alias);
        this.totals = totals;
    }

    public AttributeInGrid(String uri) {
        super(uri, "");
        totals = new ArrayList<>();
    }

    public AttributeInGrid(String uri, String alias) {
        super(uri, alias);
        totals = new ArrayList<>();
    }

    public Collection<Collection<String>> getTotals() {
        final LinkedList<Collection<String>> result = new LinkedList<>();
        for (final Collection<String> t : totals) {
            result.add(t);
        }
        return result;
    }
}
