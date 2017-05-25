/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.collections;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.gooddata.collections.PageableList.ITEMS_NODE;
import static com.gooddata.collections.PageableList.LINKS_NODE;
import static com.gooddata.collections.PageableList.PAGING_NODE;
import static com.gooddata.util.Validate.notNull;

public abstract class PageableListDeserializer<T, E> extends JsonDeserializer<T> {

    private static final TypeReference<Map<String, String>> LINKS_TYPE = new TypeReference<Map<String, String>>() {};

    private final Class<E> elementType;
    private final String collectionName;

    protected PageableListDeserializer(final Class<E> elementType) {
        this(elementType, ITEMS_NODE);
    }

    protected PageableListDeserializer(final Class<E> elementType, final String collectionName) {
        this.elementType = notNull(elementType, "elementType");
        this.collectionName = notNull(collectionName, "collectionName");
    }

    protected abstract T createList(final List<E> items, final Paging paging, final Map<String, String> links);

    @Override
    public T deserialize(final JsonParser jp, final DeserializationContext context) throws IOException, JsonProcessingException {
        final JsonNode root = jp.readValueAsTree();
        if (root == null || root.isNull()) {
            return null;
        }

        // codec should be always instance of ObjectMapper so this cast should be safe
        // we do not want to create custom object mapper with different configuration than the global one has
        final ObjectMapper objectMapper = (ObjectMapper) jp.getCodec();

        final JsonNode pagingNode = root.get(PAGING_NODE);
        final Paging paging = pagingNode == null ? null : objectMapper.convertValue(pagingNode, Paging.class);

        final JsonNode linksNode = root.get(LINKS_NODE);
        final Map<String, String> links;
        if (linksNode != null) {
            links = objectMapper.convertValue(linksNode, LINKS_TYPE);
        } else {
            links = null;
        }

        final JsonNode itemsNode = root.get(collectionName);
        final List<E> items;
        if (itemsNode == null) {
            items = Collections.emptyList();
        } else {
            items = new ArrayList<>(itemsNode.size());
            for (JsonNode item : itemsNode) {
                items.add(objectMapper.convertValue(item, elementType));
            }
        }
        return createList(items, paging, links);
    }

}
