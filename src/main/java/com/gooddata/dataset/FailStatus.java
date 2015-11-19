package com.gooddata.dataset;

import com.gooddata.gdc.ErrorStructure;
import com.gooddata.util.GDDateTimeDeserializer;
import com.gooddata.util.GDDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fail status of dataset load.
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FailStatus {

    private final String status;
    private final DateTime date;
    private final ErrorStructure error;
    private final List<FailPart> parts;

    @JsonCreator
    private FailStatus(@JsonProperty("status") String status,
                       @JsonProperty("date") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime date,
                       @JsonProperty("error") ErrorStructure error, @JsonProperty("parts") List<FailPart> parts) {
        this.status = status;
        this.date = date;
        this.error = error;
        this.parts = parts;
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

    /**
     *
     * @return or null
     */
    public List<FailPart> getParts() {
        return parts;
    }

    @JsonIgnore
    public List<FailPart> getErrorParts() {
        if (parts == null) {
            return Collections.emptyList();
        }
        final List<FailPart> result = new ArrayList<>(parts.size());
        for (FailPart part: parts) {
            if ("ERROR".equals(part.getStatus())) {
                result.add(part);
            }
        }
        return result;
    }
}
