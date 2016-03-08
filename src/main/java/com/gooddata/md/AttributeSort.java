/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

/**
 * Internal representation of attribute sort field. Which can be either plain text value or structure pointing to display form.
 */
@JsonDeserialize(using = AttributeSort.Deserializer.class)
@JsonSerialize(using = AttributeSort.Serializer.class)
class AttributeSort {

    static String PK = "pk";
    static String BY_USED_DF = "byUsedDF";

    private final String value;
    private final boolean linkType;

    AttributeSort(String value) {
        this(value, false);
    }

    AttributeSort(String value, boolean linkType) {
        this.value = notEmpty(value, "value");
        this.linkType = linkType;
    }

    String getValue() {
        return value;
    }

    boolean isLinkType() {
        return linkType;
    }

    static class Deserializer extends JsonDeserializer<AttributeSort> {

        @Override
        public AttributeSort deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            final JsonNode root = p.readValueAsTree();
            notNull(root, "jsonNode");
            if (root.isTextual()) {
                return new AttributeSort(root.textValue());
            } else if (root.isObject()) {
                return new AttributeSort(root.findValue("uri").textValue(), true);
            } else {
                throw ctxt.mappingException("Only textual or object node expected but %s node found",
                        root.getNodeType().name());
            }
        }
    }

    static class Serializer extends JsonSerializer<AttributeSort> {
        @Override
        public void serialize(AttributeSort value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value.linkType) {
                gen.writeStartObject();
                gen.writeFieldName("df");
                gen.writeStartObject();
                gen.writeStringField("uri", value.getValue());
                gen.writeEndObject();
                gen.writeEndObject();
            } else {
                gen.writeString(value.getValue());
            }
        }
    }
}
