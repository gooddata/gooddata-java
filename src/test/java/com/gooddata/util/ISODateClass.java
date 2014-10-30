package com.gooddata.util;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
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