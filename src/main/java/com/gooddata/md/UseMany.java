package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;

import static com.gooddata.util.Validate.notNull;

/**
 * UsedBy/Using batch result
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
class UseMany {

    private final Collection<UseManyEntries> useMany;

    @JsonCreator
    UseMany(@JsonProperty("useMany") final Collection<UseManyEntries> useMany) {
        this.useMany = notNull(useMany, "useMany");
    }

    public Collection<UseManyEntries> getUseMany() {
        return useMany;
    }
}
