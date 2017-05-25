/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import static com.fasterxml.jackson.databind.JsonMappingException.from;
import static com.gooddata.util.Validate.notNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Deserializes String or Integer 1 or 0 as boolean
 */
public class BooleanDeserializer extends JsonDeserializer<Boolean> {

    private static final Integer ONE = 1;

    @Override
    public Boolean deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        final JsonNode root = jp.readValueAsTree();
        notNull(root, "jsonNode");
        if (root.isInt()) {
            return ONE.equals(root.numberValue());
        } else if (root.isTextual()) {
            return "1".equals(root.textValue());
        } else {
            throw from(jp, "Unknown value of type: " + root.getNodeType());
        }
    }

}