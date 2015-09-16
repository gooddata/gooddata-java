package com.gooddata.util;

import static com.gooddata.util.Validate.notNull;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;

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
            return "1".equals(root.getTextValue());
        } else {
            throw new JsonMappingException("Expected textual value: " + root.asText(), jp.getCurrentLocation());
        }
    }

}