package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;

/**
 * UsedBy/Using result
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
class InUseEntries {

    private final Collection<Entry> entries;

    @JsonCreator
    InUseEntries(@JsonProperty("entries") Collection<Entry> entries) {
        this.entries = entries;
    }

    public Collection<Entry> getEntries() {
        return entries;
    }

}
