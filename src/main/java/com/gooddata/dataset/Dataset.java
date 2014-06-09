package com.gooddata.dataset;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Dataset
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dataset {
    private final String identifier;
    private final String link;
    private final String title;

    @JsonCreator
    public Dataset(@JsonProperty("identifier") String identifier, @JsonProperty("link") String link,
                   @JsonProperty("title") String title) {
        this.identifier = identifier;
        this.link = link;
        this.title = title;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }
}
