package com.gooddata.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

class ISODateClass {

    private DateTime date;

    @JsonCreator
    public ISODateClass(@JsonProperty("date") @JsonDeserialize(using = ISODateTimeDeserializer.class) final DateTime date) {
        this.date = date;
    }

    @JsonSerialize(using = ISODateTimeSerializer.class)
    public DateTime getDate() {
        return date;
    }
}