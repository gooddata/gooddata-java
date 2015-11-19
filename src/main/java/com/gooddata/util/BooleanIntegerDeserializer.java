package com.gooddata.util;

import static com.gooddata.util.Validate.notNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

/**
 * Deserializes Integer 1 or 0 as boolean
 */
public class BooleanIntegerDeserializer extends JsonDeserializer<Boolean> {

    private static final Integer ONE = 1;

    @Override
    public Boolean deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        final JsonNode root = jp.readValueAsTree();
        notNull(root, "jsonNode");
        if (root.isInt()) {
            return ONE.equals(root.numberValue());
        } else {
            throw new JsonMappingException("Expected int value: " + root.asText(), jp.getCurrentLocation());
        }
    }

}