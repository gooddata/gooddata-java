package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dataset
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dataset {
    private final String identifier;
    private final String uri;
    private final String title;

    @JsonCreator
    public Dataset(@JsonProperty("identifier") String identifier, @JsonProperty("link") String uri,
                   @JsonProperty("title") String title) {
        this.identifier = identifier;
        this.uri = uri;
        this.title = title;
    }

    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return self URI string
     * @deprecated use {@link #getUri()} instead
     */
    @Deprecated
    public String getLink() {
        return getUri();
    }

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }
}
