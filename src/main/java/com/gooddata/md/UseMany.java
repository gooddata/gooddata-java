package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

import static com.gooddata.util.Validate.notNull;

/**
 * UsedBy/Using batch result
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
