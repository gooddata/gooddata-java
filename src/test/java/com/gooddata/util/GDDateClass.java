/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class GDDateClass {
    private LocalDate date;

    @JsonCreator
    public GDDateClass(@JsonProperty("date") @JsonDeserialize(using = GDDateDeserializer.class) final LocalDate date) {
        this.date = date;
    }

    @JsonSerialize(using = GDDateSerializer.class)
    public LocalDate getDate() {
        return date;
    }

}
