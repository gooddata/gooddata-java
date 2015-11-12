package com.gooddata.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

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