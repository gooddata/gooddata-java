package com.gooddata.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

class GDDateTimeClass {

    private DateTime date;

    @JsonCreator
    public GDDateTimeClass(@JsonProperty("date") @JsonDeserialize(using = GDDateTimeDeserializer.class) final DateTime date) {
        this.date = date;
    }

    @JsonSerialize(using = GDDateTimeSerializer.class)
    public DateTime getDate() {
        return date;
    }
}
