package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
