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
import org.joda.time.DateTime;

class ISODateClass {

    private final DateTime date;

    @JsonCreator
    public ISODateClass(@JsonProperty("date") @JsonDeserialize(using = ISODateTimeDeserializer.class) final DateTime date) {
        this.date = date;
    }

    @JsonSerialize(using = ISODateTimeSerializer.class)
    public DateTime getDate() {
        return date;
    }
}