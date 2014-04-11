/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Item {

    private final String uri;
    protected String alias = "";

    @JsonCreator
    public Item(@JsonProperty("uri") String uri, @JsonProperty("alias") String alias) {
        this.uri = uri;
        this.alias = alias;
    }

    public Item(String uri) {
        this(uri, "");
    }

    public String getUri() {
        return uri;
    }

    public String getAlias() {
        return alias;
    }

}
