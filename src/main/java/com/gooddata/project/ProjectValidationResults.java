/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.gooddata.util.BooleanDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.LinkedList;
import java.util.List;

/**
 * Results of project validation.
 *
 * Deserialization only.
 */
@JsonTypeName("projectValidateResult")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectValidationResults {

    private final boolean error;
    private final boolean fatalError;
    private final boolean warning;

    private final List<ProjectValidationResult> results = new LinkedList<>();

    @JsonCreator
    private ProjectValidationResults(@JsonProperty("error_found") @JsonDeserialize(using = BooleanDeserializer.class) boolean error,
                                     @JsonProperty("fatal_error_found") @JsonDeserialize(using = BooleanDeserializer.class) boolean fatalError,
                                     @JsonProperty("results") List<ProjectValidationResultItem> resultItems) {
        this.error = error;
        this.fatalError = fatalError;

        boolean hasWarning = false;
        for (ProjectValidationResultItem resultItem : resultItems) {
            final List<ProjectValidationResult> itemResults = resultItem.getLogs();
            if (itemResults != null) {
                for (ProjectValidationResult log : itemResults) {
                    log.setValidation(resultItem.getValidation());
                    this.results.add(log);
                    hasWarning = hasWarning || log.isWarning();
                }
            }
        }

        this.warning = hasWarning;
    }

    /**
     * True if validation found some error
     * @return true in case of validation error, false otherwise
     */
    public boolean isError() {
        return error;
    }

    /**
     * True in case some part of validation crashed (not executed at all)
     * @return true if validation crashed, false otherwise
     */
    public boolean isFatalError() {
        return fatalError;
    }

    /**
     * True if some warning was found during validation.
     * @return true if some warning was found during validation, false otherwise
     */
    public boolean isWarning() {
        return warning;
    }

    /**
     * True if no warning or error is in the validation results.
     * @return true if no warning or error is in the validation results, false otherwise
     */
    public boolean isValid() {
        return ! (error || fatalError || warning);
    }

    /**
     * Get validation results, describing output of all validations executed.
     * Can return empty list, in case the validation had no error or warning (is valid)-
     * @return validation results list
     */
    public List<ProjectValidationResult> getResults() {
        return results;
    }

}
