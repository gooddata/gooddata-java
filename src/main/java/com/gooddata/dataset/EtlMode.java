package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonTypeName("etlMode")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
class EtlMode {

    public static final String URL = "/gdc/md/{project}/etl/mode";

    private final EtlModeType mode;

    private final LookupMode lookup;

    @JsonCreator
    public EtlMode(@JsonProperty("mode") final EtlModeType mode,
                   @JsonProperty("lookup") final LookupMode lookup) {
        this.mode = mode;
        this.lookup = lookup;
    }

    public EtlModeType getMode() {
        return mode;
    }

    public LookupMode getLookup() {
        return lookup;
    }
}
