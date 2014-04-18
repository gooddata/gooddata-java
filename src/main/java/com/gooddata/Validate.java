package com.gooddata;

import java.util.Collection;
import java.util.Iterator;

/**
 * Validation utils
 */
public abstract class Validate {

    public static <T> T notNull(T value, String aargument) {
        if (value == null) {
            throw new IllegalArgumentException(aargument + " can't be null");
        }
        return value;
    }

    public static <T extends CharSequence> T notEmpty(T value, String argument) {
        notNull(value, argument);
        if (value.toString().trim().length() == 0) {
            throw new IllegalArgumentException(argument + " can't be empty");
        }
        return value;
    }

    public static <T extends Collection> T noNullElements(T collection, String argument) {
        notNull(collection, argument);
        int i = 0;
        for (Iterator it = collection.iterator(); it.hasNext(); i++) {
            if (it.next() == null) {
                throw new IllegalArgumentException(argument + " contains null element at index: " + i);
            }
        }
        return collection;
    }

}
