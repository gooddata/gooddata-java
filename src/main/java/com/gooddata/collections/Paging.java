package com.gooddata.collections;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Description of the current collection page. Deserialization only.
 */
@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.NONE)
@JsonTypeName("paging")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Paging {

    private final int offset;
    private final int count;
    private final UriPage next;

    @JsonCreator
    public Paging(@JsonProperty("offset") final int offset,
                  @JsonProperty("count") final int count,
                  @JsonProperty("next") final String next) {
        this.offset = offset;
        this.count = count;
        this.next = next == null ? null : new UriPage(next);
    }

    public int getOffset() {
        return offset;
    }

    public int getCount() {
        return count;
    }

    public UriPage getNext() {
        return next;
    }
}
