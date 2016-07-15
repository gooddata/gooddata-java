package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.gdc.AboutLinks;
import com.gooddata.gdc.AboutLinks.Link;

/**
 * Dataset link
 * @deprecated use {@link Link} instead
 */
@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dataset extends Link {

    @JsonCreator
    public Dataset(@JsonProperty("identifier") String identifier, @JsonProperty("link") String uri,
                   @JsonProperty("title") String title) {
        super(identifier, uri, title, null, null);
    }
}
