/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.List;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;
import static java.util.Collections.emptyList;

/**
 * {@link ExecutionResult}'s warning.
 */
public class Warning {
    private final String warningCode;
    private final String message;
    private final List<Object> parameters;

    /**
     * Creates new instance
     * @param warningCode error code
     * @param message message
     */
    public Warning(final String warningCode, final String message) {
        this(warningCode, message, emptyList());
    }

    /**
     * Creates new instance
     * @param warningCode error code
     * @param message message
     * @param parameters message's parameters
     */
    @JsonCreator
    public Warning(@JsonProperty("warningCode") final String warningCode,
                   @JsonProperty("message") final String message,
                   @JsonProperty("parameters") final List<Object> parameters) {
        this.warningCode = notEmpty(warningCode, "warningCode");
        this.message = notEmpty(message, "message");
        this.parameters = notNull(parameters, "parameters");
    }

    /**
     * @return warning's error code
     */
    public String getWarningCode() {
        return warningCode;
    }

    /**
     * @return warning's message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return message's parameters
     */
    public List<Object> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Warning warning = (Warning) o;

        if (warningCode != null ? !warningCode.equals(warning.warningCode) : warning.warningCode != null) return false;
        if (message != null ? !message.equals(warning.message) : warning.message != null) return false;
        return parameters != null ? parameters.equals(warning.parameters) : warning.parameters == null;
    }

    @Override
    public int hashCode() {
        int result = warningCode != null ? warningCode.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
