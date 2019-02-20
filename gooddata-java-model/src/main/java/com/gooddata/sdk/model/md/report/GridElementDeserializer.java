/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

import static com.gooddata.sdk.model.md.report.MetricGroup.METRIC_GROUP;

/**
 * Custom deserializer for {@link GridElement}'s implementations
 */
class GridElementDeserializer extends JsonDeserializer<GridElement> {

    @Override
    public GridElement deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        switch (jp.currentToken()) {
            case VALUE_STRING:
                final String textValue = ctxt.readValue(jp, String.class);
                if (MetricGroup.equals(textValue)) {
                    return METRIC_GROUP;
                } else {
                    return (GridElement) ctxt.handleWeirdStringValue(GridElement.class, textValue,
                            "Unknown string representation of GridElement: %s", textValue);
                }
            case START_OBJECT:
                return ctxt.readValue(jp, AttributeInGrid.class);
            default:
                return (GridElement) ctxt.handleUnexpectedToken(GridElement.class, jp.currentToken(), jp,
                        "Unknown type of GridElement");
        }
    }
}
