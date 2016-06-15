/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Set;

/**
 * Serializes set of tags (strings) to whitespace separated string of tags
 */
public class TagsSerializer extends JsonSerializer<Set<String>> {

    @Override
    public void serialize(Set<String> set, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(StringUtils.join(set, " "));
    }
}