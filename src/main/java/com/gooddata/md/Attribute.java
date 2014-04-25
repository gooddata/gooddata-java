/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;

/**
 * Attribute
 */
@JsonTypeName("attribute")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Attribute extends Obj implements Queryable {

    private final Content content;

    @JsonCreator
    public Attribute(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    public Content getContent() {
        return content;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public static class Content {
        private final Collection<Key> pk;
        private final Collection<Key> fk;
        private final Collection<DisplayForm> displayForms;

        @JsonCreator
        public Content(@JsonProperty("pk") Collection<Key> pk, @JsonProperty("fk") Collection<Key> fk,
                       @JsonProperty("displayForms") Collection<DisplayForm> displayForms) {
            this.pk = pk;
            this.fk = fk;
            this.displayForms = displayForms;
        }

        public Collection<Key> getPk() {
            return pk;
        }

        public Collection<Key> getFk() {
            return fk;
        }

        public Collection<DisplayForm> getDisplayForms() {
            return displayForms;
        }
    }

}
