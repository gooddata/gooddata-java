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
 * Description of the current collection page.
 */
@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.NONE)
@JsonTypeName("paging")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Paging {

    private final String offset;
    private final UriPage next;

    @JsonCreator
    public Paging(@JsonProperty("offset") final String offset, @JsonProperty("next") final String next) {
        this.offset = offset;
        this.next = next == null ? null : new UriPage(next);
    }

    @Deprecated
    public Paging(final int offset, final int count, final String next) {
        this(Integer.toString(offset), next);
    }

    public Paging(final String next) {
        this(null, next);
    }

    @JsonIgnore
    public String getOffset() {
        return offset;
    }

    @JsonIgnore
    public UriPage getNext() {
        return next;
    }

    @JsonProperty("next")
    public String getNextUri() {
        return next == null ? null : next.getPageUri(null).toString();
    }
}
