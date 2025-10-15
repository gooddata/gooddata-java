/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.List;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Represents elements of attribute
 */
@JsonTypeName("attributeElements")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeElements {

    static final String URI = Obj.OBJ_URI + "/elements";

    private final List<AttributeElement> elements;

    @JsonCreator
    AttributeElements(@JsonProperty("elements") List<AttributeElement> elements) {
        notNull(elements, "elements");
        this.elements = elements;
    }

    public List<AttributeElement> getElements() {
        return elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AttributeElements that = (AttributeElements) o;

        return !(elements != null ? !elements.equals(that.elements) : that.elements != null);

    }

    @Override
    public int hashCode() {
        return elements != null ? elements.hashCode() : 0;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}

