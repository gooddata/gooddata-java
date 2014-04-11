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

/**
 */
@JsonTypeName("attribute")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AttributeItem extends Item {

    private Collection<Totals> totals = new ArrayList<>();

    @JsonCreator
    public AttributeItem(@JsonProperty("uri") String uri, @JsonProperty("totals") Collection<Totals> totals,
                         @JsonProperty("alias") String alias) {
        super(uri, alias);
        this.totals = totals;
    }

    public AttributeItem(String uri) {
        super(uri, "");
    }

    public Collection<Totals> getTotals() {
        return totals;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public static class Totals {
        private Collection<String> totals = new ArrayList<>();

        @JsonCreator
        public Totals(@JsonProperty Collection<String> totals) {
            this.totals = totals;
        }

        public Collection<String> getTotals() {
            return totals;
        }
    }
}
