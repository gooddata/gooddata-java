package com.gooddata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
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
        return FORMATTER.parseDateTime(root.textValue());
    }
}
