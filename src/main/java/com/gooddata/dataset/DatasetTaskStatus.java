package com.gooddata.dataset;

import com.gooddata.gdc.AbstractTaskStatus;
import com.gooddata.gdc.GdcError;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;
import java.util.Collections;

/**
 * Dataset asynchronous task status.
 * Deserialization only.
 */
class DatasetTaskStatus extends AbstractTaskStatus {

    @JsonCreator
    private DatasetTaskStatus(@JsonProperty("status") String status, @JsonProperty("poll") String pollUri,
                              @JsonProperty("messages") Collection<GdcError> messages) {
        super(status, pollUri, messages);
    }

    DatasetTaskStatus(String status, String pollUri) {
        this(status, pollUri, Collections.<GdcError>emptyList());
    }

}
