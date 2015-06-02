/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;

/**
 * Attribute representation which is nested in some other metadata object (i.e. within {@link Dimension}).
 * Can't be queried, get or updated directly - use {@link Attribute} for these operations.
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class NestedAttribute extends AbstractObj {

    @JsonProperty("content")
    private final Content content;

    @JsonCreator
    protected NestedAttribute(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    @JsonIgnore
    public Collection<DisplayForm> getDisplayForms() {
        return content.getDisplayForms();
    }

    @JsonIgnore
    public Collection<Key> getPrimaryKeys() {
        return content.getPk();
    }

    @JsonIgnore
    public Collection<Key> getForeignKeys() {
        return content.getFk();
    }

    @JsonIgnore
    public DisplayForm getDefaultDisplayForm() {
        return getDisplayForms().iterator().next();
    }

    @JsonIgnore
    public String getDimensionLink() {
        return content.getDimensionLink();
    }

    public boolean hasDimension() {
        return getDimensionLink() != null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    protected static class Content {
        private final Collection<Key> pk;
        private final Collection<Key> fk;
        private final Collection<DisplayForm> displayForms;
        private final String dimension;

        @JsonCreator
        public Content(@JsonProperty("pk") Collection<Key> pk, @JsonProperty("fk") Collection<Key> fk,
                       @JsonProperty("displayForms") Collection<DisplayForm> displayForms, @JsonProperty("dimension") String dimension) {
            this.pk = pk;
            this.fk = fk;
            this.displayForms = displayForms;
            this.dimension = dimension;
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

        @JsonProperty("dimension")
        public String getDimensionLink() {
            return dimension;
        }
    }

}
