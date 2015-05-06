package com.gooddata.util;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
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
