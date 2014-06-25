package com.gooddata;

import java.util.Collection;
import java.util.Iterator;

/**
 * Validation utils
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

}
