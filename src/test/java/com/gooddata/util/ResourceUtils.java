/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.util;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static java.lang.String.format;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResourceUtils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T readObjectFromResource(Class testClass, String resourcePath, Class<T> objectClass) {
        notNull(testClass, "testClass");
        notEmpty(resourcePath, "resourcePath");
        notNull(objectClass, "objectClass");

        try {
            return OBJECT_MAPPER.readValue(testClass.getResourceAsStream(resourcePath), objectClass);
        } catch (IOException e) {
            throw new IllegalStateException(format("Cannot read class %s from resource %s", objectClass, resourcePath), e);
        }
    }

    public static String readStringFromResource(String resourcePath) {
        notEmpty(resourcePath, "resourcePath");

        try {
            return IOUtils.toString(ResourceUtils.class.getResourceAsStream(resourcePath),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(format("Cannot read from resource %s", resourcePath), e);
        }
    }
}
