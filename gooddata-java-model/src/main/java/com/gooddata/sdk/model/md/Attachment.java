/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;

/**
 * Common ancestor to {@link ScheduledMail} attachments.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "dashboardAttachment", value = DashboardAttachment.class),
        @JsonSubTypes.Type(name = "reportAttachment", value = ReportAttachment.class),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attachment implements Serializable {

    private static final long serialVersionUID = -3210630720136793710L;
    private final String uri;

    @JsonCreator
    protected Attachment(@JsonProperty("uri") String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attachment that = (Attachment) o;

        return !(uri != null ? !uri.equals(that.uri) : that.uri != null);
    }

    @Override
    public int hashCode() {
        return uri != null ? uri.hashCode() : 0;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
