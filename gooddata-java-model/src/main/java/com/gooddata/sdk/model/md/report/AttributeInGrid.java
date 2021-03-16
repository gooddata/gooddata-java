/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.model.md.Attribute;
import com.gooddata.sdk.model.md.DisplayForm;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Attribute in Grid
 */
@JsonTypeName("attribute")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeInGrid implements GridElement, Serializable {

    private static final long serialVersionUID = 9061580138440068825L;
    private static final String DISPLAY_FORM_ARG_NAME = "displayForm";
    private static final String ATTRIBUTE_ARG_NAME = "attribute";

    private final String uri;
    private final String alias;
    private final List<List<String>> totals;

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
     * @param alias alias used to label the attribute
     */
    public AttributeInGrid(String uri, String alias) {
        this(uri, new ArrayList<>(), alias);
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
            totalStringList.addAll(totalList.stream().map(Total::toString).collect(Collectors.toList()));
            this.totals.add(totalStringList);
        }
    }

    /**
     * Creates new AttributeInGrid using given DisplayForm's uri and it's title as alias.
     * @param displayForm displayForm to create AttributeInGrid from
     */
    public AttributeInGrid(final DisplayForm displayForm) {
        this(notNull(notNull(displayForm, DISPLAY_FORM_ARG_NAME).getUri(), "uri"),
                notNull(displayForm, DISPLAY_FORM_ARG_NAME).getTitle());
    }

    /**
     * Creates new AttributeInGrid using given DisplayForm's uri and given alias.
     * @param displayForm displayForm to create AttributeInGrid from
     * @param alias alias used to label the attribute
     */
    public AttributeInGrid(final DisplayForm displayForm, final String alias) {
        this(notNull(notNull(displayForm, DISPLAY_FORM_ARG_NAME).getUri(), "uri"), alias);
    }

    /**
     * Creates new AttributeInGrid using given Attribute's default DisplayForm's uri and Attribute's title as alias.
     * @param attribute attribute to create AttributeInGrid from
     */
    public AttributeInGrid(final Attribute attribute) {
        this(notNull(attribute, ATTRIBUTE_ARG_NAME).getDefaultDisplayForm(),
                notNull(attribute, ATTRIBUTE_ARG_NAME).getTitle());
    }

    /**
     * Creates new AttributeInGrid using given Attribute's default DisplayForm's uri and given alias.
     * @param attribute attribute to create AttributeInGrid from
     * @param alias alias used to label the attribute
     */
    public AttributeInGrid(final Attribute attribute, final String alias) {
        this(notNull(attribute, ATTRIBUTE_ARG_NAME).getDefaultDisplayForm(), alias);
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
            totalEnumList.addAll(totalList.stream().map(Total::of).collect(Collectors.toList()));
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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
