/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Objects;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Metadata query restriction. See static factory methods to get instance of desired restriction type.
 */
public class Restriction {

    private final Type type;

    private final String value;

    private Restriction(Type type, String value) {
        this.type = notNull(type, "type");
        this.value = notNull(value, "value");
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

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Restriction that = (Restriction) o;
        return type == that.type &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    public enum Type {
        IDENTIFIER, TITLE, SUMMARY
    }
}
