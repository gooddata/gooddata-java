package com.gooddata.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;

/**
 * TODO
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("manage")
public class MaqlDdl {

    public static final String URI = "/gdc/md/{project}/ldm/manage2";

    private final String maql;

    @JsonCreator
    public MaqlDdl(@JsonProperty("maql") String maql) {
        this.maql = maql;
    }

    public String getMaql() {
        return maql;
    }
}
