/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

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