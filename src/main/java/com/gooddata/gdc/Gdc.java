/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.gdc.AboutLinks.Link;

import java.util.List;

/**
 * GoodData API root links (aka "about" or "home").
 * Deserialization only.
 * @deprecated use {@link RootLinks} instead
 */
@Deprecated
public class Gdc extends RootLinks {

    /**
     * URI of GoodData API root
     */
    public static final String URI = "/gdc";

    @JsonCreator
    public Gdc(@JsonProperty("links") List<Link> links) {
        super(links);
    }

}