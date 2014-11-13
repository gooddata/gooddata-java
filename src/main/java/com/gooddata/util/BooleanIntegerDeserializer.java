package com.gooddata.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;

/**
 * Deserializes Integer 1 or 0 as boolean
 */
public class BooleanIntegerDeserializer extends JsonDeserializer<Boolean> {

    private static final Integer ONE = 1;

    @Override
    public Boolean deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        final JsonNode root = jp.readValueAsTree();
        return root != null && ONE.equals(root.getNumberValue());
    }

}