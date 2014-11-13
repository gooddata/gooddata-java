package com.gooddata.util;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class BooleanIntegerClass {
    private final boolean foo;

    @JsonCreator
    public BooleanIntegerClass(@JsonProperty("foo") @JsonDeserialize(using = BooleanIntegerDeserializer.class) final boolean foo) {
        this.foo = foo;
    }

    @JsonSerialize(using = BooleanIntegerSerializer.class)
    public boolean isFoo() {
        return foo;
    }
}
