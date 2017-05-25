/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.TokenBuffer;

import java.io.IOException;

/**
 * Common deserializer for {@link ErrorStructure} and {@link GdcError}.
 */
class ErrorStructureDeserializer extends JsonDeserializer<ErrorStructure> {

    private static final String GDC_ERROR_TYPE_NAME = "error";

    @Override
    public ErrorStructure deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        final TokenBuffer tokenBuffer = new TokenBuffer(jp);
        tokenBuffer.copyCurrentStructure(jp);

        final TreeNode treeNode = tokenBuffer.asParser().readValueAsTree();

        Class<? extends ErrorStructure> clazz;
        if (treeNode.isObject()) {
            if (((ObjectNode) treeNode).has(GDC_ERROR_TYPE_NAME)) {
                clazz = GdcError.class;
            } else {
                clazz = DefaultDeserializerErrorStructure.class;
            }
        } else {
            throw ctxt.mappingException("Unknown type of ErrorStructure");
        }

        final JsonParser nextParser = tokenBuffer.asParser();
        nextParser.nextToken(); // just created parser points before the first token

        return ctxt.readValue(nextParser, clazz);
    }

    /**
     * This class actually represents deserialized {@link ErrorStructure}.
     * We need to override deserializer to break the cycle while deserialize.
     */
    @JsonDeserialize(using = None.class)
    private static class DefaultDeserializerErrorStructure extends ErrorStructure {

        protected DefaultDeserializerErrorStructure(@JsonProperty("errorClass") String errorClass,
                                                    @JsonProperty("component") String component,
                                                    @JsonProperty("parameters") Object[] parameters,
                                                    @JsonProperty("message") String message,
                                                    @JsonProperty("errorCode") String errorCode,
                                                    @JsonProperty("errorId") String errorId,
                                                    @JsonProperty("trace") String trace,
                                                    @JsonProperty("requestId") String requestId) {
            super(errorClass, component, parameters, message, errorCode, errorId, trace, requestId);
        }
    }
}
