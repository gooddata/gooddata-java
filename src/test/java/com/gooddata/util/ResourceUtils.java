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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceUtils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T readObjectFromResource(Class testClass, String resourcePath, Class<T> objectClass) {
        notNull(objectClass, "objectClass");

        try {
            return OBJECT_MAPPER.readValue(readFromResource(resourcePath, testClass), objectClass);
        } catch (IOException e) {
            throw new IllegalStateException(format("Cannot read class %s from resource %s", objectClass, resourcePath), e);
        }
    }

    public static String readStringFromResource(String resourcePath) {
        try {
            return IOUtils.toString(readFromResource(resourcePath),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(format("Cannot read from resource %s", resourcePath), e);
        }
    }

    public static InputStream readFromResource(String resourcePath) {
        final Class<?> clazz = ResourceUtils.class;
        return readFromResource(resourcePath, clazz);
    }



    private static InputStream readFromResource(String resourcePath, Class<?> testClass) {
        notEmpty(resourcePath, "resourcePath");
        notNull(testClass, "testClass cannot be null!");

        return testClass.getResourceAsStream(resourcePath);
    }
}
