/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.LocalDate;

public class GDDateClass {
    private final LocalDate date;

    @JsonCreator
    public GDDateClass(@JsonProperty("date") @JsonDeserialize(using = GDDateDeserializer.class) final LocalDate date) {
        this.date = date;
    }

    @JsonSerialize(using = GDDateSerializer.class)
    public LocalDate getDate() {
        return date;
    }

}
