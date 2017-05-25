/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Argument validation helper methods used internally by GoodData SDK.
 */
public abstract class Validate {

    /**
     * Throws IllegalArgumentException if the value is null, otherwise returns the value.
     *
     * @param value        input value
     * @param argumentName the name of input argument
     * @param <T>          the type of the argument
     * @return the value
     */
    public static <T> T notNull(T value, String argumentName) {
        if (value == null) {
            throw new IllegalArgumentException(argumentName + " can't be null");
        }
        return value;
    }

    /**
     * Throws IllegalArgumentException if the char sequence is empty, otherwise returns the char sequence.
     *
     * @param value        input char sequence
     * @param argumentName the name of input argument
     * @param <T>          the type of char sequence
     * @return the char sequence
     */
    public static <T extends CharSequence> T notEmpty(T value, String argumentName) {
        notNull(value, argumentName);
        if (value.toString().trim().length() == 0) {
            throw new IllegalArgumentException(argumentName + " can't be empty");
        }
        return value;
    }

    /**
     * Throws IllegalArgumentException if the map is empty, otherwise returns the map.
     *
     * @param value        input collection
     * @param argumentName the name of the input argument
     * @param <T>          the type of map
     * @return map
     */
    public static <T extends Collection> T notEmpty(T value, String argumentName) {
        notNull(value, argumentName);
        if (value.size() == 0) {
            throw new IllegalArgumentException(argumentName + " can't be empty");
        }
        return value;
    }

    /**
     * Throws IllegalArgumentException if the collection contains null elements (or is null), otherwise returns
     * the collection.
     *
     * @param collection   input collection
     * @param argumentName the name of input argument
     * @param <T>          the type of the collection
     * @return the collection
     */
    public static <T extends Collection> T noNullElements(T collection, String argumentName) {
        notNull(collection, argumentName);
        int i = 0;
        for (Iterator it = collection.iterator(); it.hasNext(); i++) {
            if (it.next() == null) {
                throw new IllegalArgumentException(argumentName + " contains null element at index: " + i);
            }
        }
        return collection;
    }

    public static <T> T[] noNullElements(T[] array, String argument) {
        notNull(array, argument);
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                throw new IllegalArgumentException(argument + " contains null element at index: " + i);
            }
        }
        return array;
    }

    /**
     * Throws IllegalStateException if the value is null, otherwise returns the value.
     *
     * @param value        input value
     * @param argumentName the name of input argument
     * @param <T>          the type of the argument
     * @return the value
     */
    public static <T> T notNullState(T value, String argumentName) {
        if (value == null) {
            throw new IllegalStateException(argumentName + " is null");
        }
        return value;
    }

    /**
     * throws new {@link IllegalArgumentException} if expression is false
     *
     * @param expression boolean expression
     * @param message    of exception
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }
}
