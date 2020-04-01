/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Serializer of {@link Users}
 */
class UsersSerializer extends JsonSerializer<Users> {

    @Override
    public void serialize(final Users users, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeFieldName(Users.ROOT_NODE);

        jgen.writeStartArray();
        final ObjectCodec codec = jgen.getCodec();
        for (Object item: users.getPageItems()) {
            codec.writeValue(jgen, item);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }
}
