package com.gooddata.util;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.joda.time.DateTime;

import static com.gooddata.util.ISODateTimeSerializer.FORMATTER;

/**
 * Deserializes Joda's {@link DateTime} fields from the ISO date time format in the UTC timezone (yyyy-MM-dd'T'HH:mm:ss.SSSZZ).
 */
public class ISODateTimeDeserializer extends JsonDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        final JsonNode root = jp.readValueAsTree();
        if (root == null || root.isNull()) {
            return null;
        }
        return FORMATTER.parseDateTime(root.getTextValue());
    }

}