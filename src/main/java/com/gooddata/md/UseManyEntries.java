package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
class UseManyEntries {

    private final String uri;

    private final Collection<Entry> entries;

    @JsonCreator
    UseManyEntries(@JsonProperty("uri") final String uri,
                   @JsonProperty("entries") final Collection<Entry> entries) {
        this.uri = uri;
        this.entries = entries;
    }

    public String getUri() {
        return uri;
    }

    public Collection<Entry> getEntries() {
        return entries;
    }
}
