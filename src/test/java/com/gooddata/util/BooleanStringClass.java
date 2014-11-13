package com.gooddata.util;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

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
