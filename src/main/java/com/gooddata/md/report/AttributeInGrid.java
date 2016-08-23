/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.md.Attribute;
import com.gooddata.md.DisplayForm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.gooddata.util.Validate.notNull;
import static java.util.Arrays.asList;

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
    private List<List<String>> totals;

    @JsonCreator
    AttributeInGrid(@JsonProperty("uri") String uri, @JsonProperty("totals") List<List<String>> totals,
                    @JsonProperty("alias") String alias) {
        this.uri = uri;
        this.alias = alias;
        this.totals = totals;
    }

    /**
     * Creates new instance.
     * @param uri uri of displayForm of attribute to be in grid
     */
    public AttributeInGrid(String uri) {
        this(uri, null);
    }

    /**
     * Creates new instance.
     * @param uri uri of displayForm of attribute to be in grid
     * @param alias alias used to label the attribute
     */
    public AttributeInGrid(String uri, String alias) {
        this(uri, new ArrayList<List<String>>(), alias);
    }

    /**
     * Creates new instance.
     * @param uri uri of displayForm of attribute to be in grid
     * @param alias alias used to label the attribute
     * @param totals totals for metrics used in grid - for each {@link MetricElement} in grid, there can be list
     *               of totals. The totals are evaluated in given order.
     */
    public AttributeInGrid(String uri, String alias, List<List<Total>> totals) {
        this(uri, alias);
        notNull(totals, "totals");
        for (List<Total> totalList : totals) {
            final List<String> totalStringList = new ArrayList<>(totalList.size());
            for (Total total : totalList) {
                totalStringList.add(total.toString());
            }
            this.totals.add(totalStringList);
        }
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

    @JsonProperty("totals")
    public List<List<String>> getStringTotals() {
       return totals;
    }

    @JsonIgnore
    public List<List<Total>> getTotals() {
        final List<List<Total>> enumTotals = new ArrayList<>(totals.size());
        for (List<String> totalList : totals) {
            final List<Total> totalEnumList = new ArrayList<>(totalList.size());
            for (String total : totalList) {
                totalEnumList.add(Total.of(total));
            }
            enumTotals.add(totalEnumList);
        }
        return enumTotals;
    }

    public String getUri() {
        return uri;
    }

    public String getAlias() {
        return alias;
    }
}
