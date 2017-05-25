/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.collections;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;

import static com.gooddata.collections.PageableList.ITEMS_NODE;
import static com.gooddata.collections.PageableList.LINKS_NODE;
import static com.gooddata.collections.PageableList.PAGING_NODE;
import static com.gooddata.util.Validate.notEmpty;

/**
 * Serializer {@link PageableList} objects into JSON.
 */
public abstract class PageableListSerializer extends JsonSerializer<PageableList<?>> {

    private final String rootNode;

    public PageableListSerializer(String rootNode) {
        this.rootNode = notEmpty(rootNode, "rootNode");
    }

    @Override
    public void serialize(final PageableList<?> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeFieldName(rootNode);

        jgen.writeStartObject();
        jgen.writeFieldName(ITEMS_NODE);

        jgen.writeStartArray();
        final ObjectCodec codec = jgen.getCodec();
        for (Object item: value) {
            codec.writeValue(jgen, item);
        }
        jgen.writeEndArray();

        writeObjectOrEmpty(jgen, codec, PAGING_NODE, value.getPaging());

        writeObjectOrEmpty(jgen, codec, LINKS_NODE, value.getLinks());

        jgen.writeEndObject();
        jgen.writeEndObject();
    }

    @Override
    public void serializeWithType(final PageableList<?> value, final JsonGenerator gen, final SerializerProvider provider,
                                  final TypeSerializer typeSer) throws IOException {
        serialize(value, gen, provider);
    }

    private void writeObjectOrEmpty(final JsonGenerator jgen, final ObjectCodec codec, final String name, final Object object) throws IOException {
        jgen.writeFieldName(name);
        if (object != null) {
            codec.writeValue(jgen, object);
        } else {
            jgen.writeStartObject();
            jgen.writeEndObject();
        }
    }
}
