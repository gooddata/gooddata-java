package com.gooddata.util;

import static com.gooddata.util.Validate.notNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

/**
 * Deserializes String "1" or "0" as boolean
 */
public class BooleanStringDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        final JsonNode root = jp.readValueAsTree();
        notNull(root, "jsonNode");
        if (root.isTextual()) {
            return "1".equals(root.textValue());
        } else {
            throw new JsonMappingException("Expected textual value: " + root.asText(), jp.getCurrentLocation());
        }
    }

}