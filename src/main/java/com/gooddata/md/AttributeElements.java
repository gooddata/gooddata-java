/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md;

import static org.apache.commons.lang.Validate.notNull;

import org.apache.commons.lang.Validate;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Set;

/**
 * Represents elements of attribute
 */
@JsonTypeName("attributeElements")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
class AttributeElements {

    static final String URI = Obj.OBJ_URI + "/elements";
    static final UriTemplate TEMPLATE = new UriTemplate(URI);

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
}
