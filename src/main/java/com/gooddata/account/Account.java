/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.account;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.springframework.web.util.UriTemplate;

/**
 */
@JsonTypeName("accountSetting")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    public static final String URI = "/gdc/account/profile/{id}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    public static final String LOGIN_URI = "/gdc/account/login/{id}";
    public static final UriTemplate LOGIN_TEMPLATE = new UriTemplate(LOGIN_URI);

    public static final String CURRENT_ID = "current";

    private final Links links;

    @JsonCreator
    public Account(@JsonProperty("links") Links links) {
        this.links = links;
    }

    public Links getLinks() {
        return links;
    }

    public String getId() {
        return TEMPLATE.match(getLinks().getSelf()).get("id");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links {
        private final String self;

        @JsonCreator
        public Links(@JsonProperty("self") String self) {
            this.self = self;
        }

        public String getSelf() {
            return self;
        }
    }
}
