package com.gooddata.util;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

/**
 * Serializes boolean as Integer 1 or 0
 */
public class BooleanIntegerSerializer extends JsonSerializer<Boolean> {

    @Override
    public void serialize(Boolean value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        gen.writeNumber(Boolean.TRUE.equals(value) ? 1 : 0);
    }
}