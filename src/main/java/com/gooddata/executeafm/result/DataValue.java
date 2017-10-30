/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.result;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.IOException;

import static com.gooddata.util.Validate.notNull;

/**
 * Simple value type of {@link Data}. Wrapper of textual value.
 */
public class DataValue implements Data {

    private final String value;

    /**
     * Creates new instance of given value.
     * @param value textual value, can't be null
     */
    public DataValue(final String value) {
        this.value = notNull(value, "value");
    }

    @JsonValue
    private String getValue() {
        return value;
    }

    @Override
    public boolean isList() {
        return false;
    }

    @Override
    public boolean isValue() {
        return true;
    }

    @Override
    public String textValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataValue dataValue = (DataValue) o;

        return value != null ? value.equals(dataValue.value) : dataValue.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
