/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

/**
 * Metadata query restriction. See static factory methods to get instance of desired restriction type.
 */
public class Restriction {

    private final Type type;

    private final String value;

    private Restriction(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    /**
     * Construct a new instance with restriction type identifier and given value.
     *
     * @param value identifier you want to search for
     * @return new restriction for identifier restriction
     */
    public static Restriction identifier(String value) {
        return new Restriction(Type.IDENTIFIER, value);
    }

    /**
     * Construct a new instance with restriction type title and given value.
     *
     * @param value title you want to search for
     * @return new restriction for title restriction
     */
    public static Restriction title(String value) {
        return new Restriction(Type.TITLE, value);
    }

    /**
     * Construct a new instance with restriction type summary and given value.
     *
     * @param value summary you want to search for
     * @return new restriction for summary restriction
     */
    public static Restriction summary(String value) {
        return new Restriction(Type.SUMMARY, value);
    }

    static enum Type {
        IDENTIFIER, TITLE, SUMMARY
    }
}
