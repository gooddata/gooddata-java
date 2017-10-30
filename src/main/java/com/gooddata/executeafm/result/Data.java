/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.result;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

/**
 * Data of {@link ExecutionResult}, can be of three basic kinds - {@link #NULL}, list and simple value.
 */
@JsonDeserialize(using = Data.DataDeserializer.class)
public interface Data {

    Data NULL = new Data() {
        @Override
        public boolean isList() {
            return false;
        }

        @Override
        public boolean isValue() {
            return false;
        }

        @Override
        @JsonValue
        public String textValue() {
            return null;
        }
    };

    /**
     * @return true if this instance is of kind list, false otherwise
     */
    boolean isList();

    /**
     * @return true if this instance is of kind value, false otherwise
     */
    boolean isValue();

    /**
     * @return true if this instance is the same as {@link #NULL}, false otherwise
     */
    default boolean isNull() {
        return this == NULL;
    }

    /**
     * @return text data value, throws exception for data which can't be represented as text
     */
    String textValue();

    /**
     * @return this instance cast to List, may throw exception if this instance is not of kind list.
     */
    default List<Data> asList() {
        throw new UnsupportedOperationException("This is not a list");
    }

    class DataDeserializer extends JsonDeserializer<Data> {
        @Override
        public Data deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            final JsonNode root = jp.readValueAsTree();
            if (root.isArray()) {
                final List<Data> list = stream(spliteratorUnknownSize(root.elements(), Spliterator.ORDERED), false)
                        .map(elem -> {
                            try {
                                return ctxt.readValue(elem.traverse(jp.getCodec()), Data.class);
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        }).collect(Collectors.toList());
                return new DataList(list);
            } else if (root.isTextual()) {
                return new DataValue(root.textValue());
            } else if (root.isNull()) {
                return NULL;
            } else {
                throw JsonMappingException.from(jp, "Unknown value of type: " + root.getNodeType());
            }
        }

        @Override
        public Data getNullValue(final DeserializationContext ctxt) throws JsonMappingException {
            return NULL;
        }
    }
}
