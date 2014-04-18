package com.gooddata.md;

/**
 * Metadata query restriction
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

    public static Restriction identifier(String value) {
        return new Restriction(Type.IDENTIFIER, value);
    }

    public static Restriction title(String value) {
        return new Restriction(Type.TITLE, value);
    }

    public static Restriction summary(String value) {
        return new Restriction(Type.SUMMARY, value);
    }

    static enum Type {
        IDENTIFIER, TITLE, SUMMARY
    }
}
