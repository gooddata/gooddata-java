package com.gooddata.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class BooleanStringClass {
    private final boolean foo;

    @JsonCreator
    public BooleanStringClass(@JsonProperty("foo") @JsonDeserialize(using = BooleanStringDeserializer.class) final boolean foo) {
        this.foo = foo;
    }

    @JsonSerialize(using = BooleanStringSerializer.class)
    public boolean isFoo() {
        return foo;
    }
}
