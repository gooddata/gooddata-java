/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Custom serializer for {@link GridElement}'s implementations
 */
class GridElementSerializer extends JsonSerializer<GridElement> {

    @Override
    public void serialize(GridElement value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value instanceof AttributeInGrid) {
            serializers.defaultSerializeValue(value, gen);
        } else if (value instanceof MetricGroup) {
            gen.writeString(((MetricGroup) value).getValue());
        } else {
            throw new JsonGenerationException("Unsupported kind of GridElement: " + value.getClass().getName(), gen);
        }
    }
}

