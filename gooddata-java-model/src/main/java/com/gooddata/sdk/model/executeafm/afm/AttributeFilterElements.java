/*
 * Copyright (C) 2007-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm;

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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.fasterxml.jackson.databind.JsonMappingException.from;

/**
 * Represents elements, which can be used as value of "in" or "notIn" by {@link AttributeFilter}.
 */
@JsonSerialize(using = AttributeFilterElements.Serializer.class)
@JsonDeserialize(using = AttributeFilterElements.Deserializer.class)
public interface AttributeFilterElements {

    /**
     * Elements the filter refers to.
     *
     * @return filter elements.
     */
    List<String> getElements();

    class Serializer extends JsonSerializer<AttributeFilterElements> {

        @Override
        public void serialize(AttributeFilterElements elements, JsonGenerator jg, SerializerProvider serializerProvider) throws IOException {
            if (elements instanceof UriAttributeFilterElements) {
                serializeWrapped(UriAttributeFilterElements.NAME, elements, jg, serializerProvider);
            } else if (elements instanceof ValueAttributeFilterElements) {
                serializeWrapped(ValueAttributeFilterElements.NAME, elements, jg, serializerProvider);
            } else {
                serializerProvider.defaultSerializeValue(elements.getElements(), jg);
            }
        }

        private static void serializeWrapped(String name, AttributeFilterElements elements, JsonGenerator jg, SerializerProvider serializerProvider) throws IOException {
            jg.writeStartObject();
            serializerProvider.defaultSerializeField(name, elements.getElements(), jg);
            jg.writeEndObject();
        }
    }

    class Deserializer extends JsonDeserializer<AttributeFilterElements> {

        @Override
        public AttributeFilterElements deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            final JsonNode node = jp.readValueAsTree();
            switch (node.getNodeType()) {
                case ARRAY:
                    return new UriAttributeFilterElements(nodeToElements(node));
                case OBJECT:
                    final JsonNode uris = node.findValue(UriAttributeFilterElements.NAME);
                    if (uris != null) {
                        return new UriAttributeFilterElements(nodeToElements(uris));
                    }
                    final JsonNode values = node.findValue(ValueAttributeFilterElements.NAME);
                    if (values != null) {
                        return new ValueAttributeFilterElements(nodeToElements(values));
                    }
                    throw from(jp, "Unknown type of AttributeFilterElements");
                default:
                    throw from(jp, "Unknown value of type: " + jp.currentToken());
            }
        }

        private static List<String> nodeToElements(JsonNode node) {
            return StreamSupport.stream(node.spliterator(), false).map(JsonNode::textValue).collect(Collectors.toList());
        }
    }


}
