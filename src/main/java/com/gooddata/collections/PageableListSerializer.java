/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.collections;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

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
