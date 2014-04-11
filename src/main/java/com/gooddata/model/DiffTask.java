package com.gooddata.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;

/**
 * TODO may be move somewhere to common
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("asyncTask")
public class DiffTask {

    private Link link;

    @JsonCreator
    public DiffTask(@JsonProperty("link") Link link) {
        this.link = link;
    }

    public String getUri() {
        return link.pollUri;
    }

    public static class Link {
        private String pollUri;

        @JsonCreator
        public Link(@JsonProperty("poll") String pollUri) {
            this.pollUri = pollUri;
        }
    }

}
