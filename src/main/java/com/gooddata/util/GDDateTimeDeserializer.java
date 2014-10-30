package com.gooddata.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.joda.time.DateTime;

import java.io.IOException;

import static com.gooddata.util.GDDateTimeSerializer.FORMATTER;

/**
 * Deserializes Joda's {@link DateTime} fields from the GoodData date time format in the UTC timezone (yyyy-MM-dd HH:mm:ss.SSSZ).
 */
public class GDDateTimeDeserializer extends JsonDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        final JsonNode root = jp.readValueAsTree();
        if (root == null || root.isNull()) {
            return null;
        }
        return FORMATTER.parseDateTime(root.getTextValue());
    }
}
