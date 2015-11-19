package com.gooddata.md.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static com.gooddata.util.Validate.notEmpty;

/**
 * Filter (in report definition)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Filter {

    private final String expression;

    @JsonCreator
    public Filter(@JsonProperty("expression") final String expression) {
        this.expression = notEmpty(expression, "expression");
    }

    public String getExpression() {
        return expression;
    }
}
