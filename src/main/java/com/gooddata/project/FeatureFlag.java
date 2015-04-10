/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriTemplate;

/**
 * Feature flag is a boolean flag used for enabling / disabling some specific feature of GoodData platform.
 */
@JsonTypeName("featureFlag")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FeatureFlag {

    public static final String FEATURE_FLAGS_URI = Project.URI + "/projectFeatureFlags";
    public static final UriTemplate FEATURE_FLAGS_TEMPLATE = new UriTemplate(FEATURE_FLAGS_URI);

    public static final String FEATURE_FLAG_URI = FEATURE_FLAGS_URI + "/{featureFlag}";
    public static final UriTemplate FEATURE_FLAG_TEMPLATE = new UriTemplate(FEATURE_FLAG_URI);

    private final String key;
    private final boolean value;

    @JsonIgnore
    private Links links;

    /**
     * Creates new feature flag which is by default enabled (true).
     *
     * @param key unique name of feature flag
     */
    public FeatureFlag(String key) {
        this(key, true, null);
    }

    /**
     * Creates new feature flag with given value.
     *
     * @param key unique name of feature flag
     * @param value true (flag enabled) or false (flag disabled)
     */
    public FeatureFlag(String key, boolean value) {
        this(key, value, null);
    }

    @JsonCreator
    private FeatureFlag(@JsonProperty("key") String key,
            @JsonProperty("value") boolean value,
            @JsonProperty("links") Links links) {
        this.key = key;
        this.value = value;
        this.links = links;
    }

    public String getKey() {
        return key;
    }

    public boolean getValue() {
        return value;
    }

    @JsonIgnore
    public String getUri() {
        return links.getSelf();
    }


    @Override
    public String toString() {
        return "FeatureFlag{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", links=" + links +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Links {
        private final String self;
        @JsonCreator
        public Links(@JsonProperty("self") String self) {
            this.self = self;
        }

        public String getSelf() {
            return self;
        }

        @Override
        public String toString() {
            return "Links{" +
                    "self='" + self + '\'' +
                    '}';
        }
    }
}
