package com.gooddata.collections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
