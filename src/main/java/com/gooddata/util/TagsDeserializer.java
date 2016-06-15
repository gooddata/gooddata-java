package com.gooddata.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static com.gooddata.util.Validate.notNull;

/**
 * Deserializes whitespace separated tags from one string to set of tags (strings)
 */
public class TagsDeserializer extends JsonDeserializer<Set<String>> {

    @Override
    public Set<String> deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        final JsonNode root = jp.readValueAsTree();
        notNull(root, "jsonNode");
        if (root.isTextual()) {
            final Scanner scanner = new Scanner(root.textValue());
            final HashSet<String> tags = new HashSet<>();
            while (scanner.hasNext()) {
                tags.add(scanner.next());
            }
            return tags;
        } else {
            throw new JsonMappingException(jp, "Unknown value of type: " + root.getNodeType(), jp.getCurrentLocation());
        }
    }

}