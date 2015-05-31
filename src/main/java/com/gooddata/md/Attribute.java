/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
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
 * Attribute of GoodData project dataset
 */
@JsonTypeName("attribute")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Attribute extends AbstractObj implements Queryable, Updatable {

    @JsonProperty("content")
    private final Content content;

    @JsonCreator
    private Attribute(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    /* Just for serialization test */
    Attribute(String title, Key primaryKey, Key foreignKey) {
        super(new Meta(title));
        content = new Content(asList(primaryKey), asList(foreignKey), Collections.<DisplayForm>emptyList());
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private static class Content {
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
