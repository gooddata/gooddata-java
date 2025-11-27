/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.IOException;
import java.io.Serializable;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Internal representation of attribute sort field. Which can be either plain text value or structure pointing to display form.
 */
@JsonDeserialize(using = AttributeSort.Deserializer.class)
@JsonSerialize(using = AttributeSort.Serializer.class)
class AttributeSort implements Serializable {

    static final String PK = "pk";
    static final String BY_USED_DF = "byUsedDF";
    private static final long serialVersionUID = -7415504020870223701L;
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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
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
                return (AttributeSort) ctxt.handleUnexpectedToken(AttributeSort.class, root.asToken(), p,
                        "Only textual or object node expected but %s node found", root.getNodeType().name());
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
