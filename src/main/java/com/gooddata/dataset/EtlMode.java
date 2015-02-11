package com.gooddata.dataset;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

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
