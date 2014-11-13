package com.gooddata.dataset;

import com.gooddata.gdc.ErrorStructure;
import com.gooddata.util.GDDateTimeDeserializer;
import com.gooddata.util.GDDateTimeSerializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;

/**
 * Fail status of dataset load.
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FailStatus {

    private final String status;
    private final DateTime date;
    private final ErrorStructure error;

    @JsonCreator
    private FailStatus(@JsonProperty("status") String status,
                       @JsonProperty("date") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime date,
                       @JsonProperty("error") ErrorStructure error) {
        this.status = status;
        this.date = date;
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    @JsonSerialize(using = GDDateTimeSerializer.class)
    public DateTime getDate() {
        return date;
    }

    public ErrorStructure getError() {
        return error;
    }
}
