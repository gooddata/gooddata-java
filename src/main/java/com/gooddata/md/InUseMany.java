/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.util.BooleanDeserializer;
import com.gooddata.util.BooleanStringSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.gooddata.util.Validate.noNullElements;
import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static java.beans.Introspector.decapitalize;

/**
 * UsedBy/Using batch request
 */
@JsonTypeName("inUseMany")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class InUseMany {

    public static final String USEDBY_URI = "/gdc/md/{projectId}/usedby2";

    private final Collection<String> uris;

    private final Set<String> types;

    private final boolean nearest;

    @JsonCreator
    InUseMany(@JsonProperty("uris") Collection<String> uris,
          @JsonProperty("nearest") @JsonDeserialize(using = BooleanDeserializer.class) boolean nearest,
          @JsonProperty("types") Set<String> types) {

        this.uris = notEmpty(uris, "uris");
        this.types = types;
        this.nearest = nearest;
    }

    @SafeVarargs
    public InUseMany(Collection<String> uris, boolean nearest, Class<? extends Obj>... type) {
        this.uris = notNull(uris, "uris");
        noNullElements(type, "type");
        this.types = new HashSet<>();
        for (Class<? extends Obj> t: type) {
            this.types.add(decapitalize(t.getSimpleName()));
        }
        this.nearest = nearest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InUseMany inUseMany = (InUseMany) o;

        if (nearest != inUseMany.nearest) return false;
        if (!types.equals(inUseMany.types)) return false;
        if (!uris.equals(inUseMany.uris)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uris.hashCode();
        result = 31 * result + types.hashCode();
        result = 31 * result + (nearest ? 1 : 0);
        return result;
    }

    public Collection<String> getUris() {
        return uris;
    }

    public Set<String> getTypes() {
        return types;
    }

    @JsonSerialize(using = BooleanStringSerializer.class)
    public boolean isNearest() {
        return nearest;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.toStringExclude(this);
    }
}
