package com.gooddata.md.report;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

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
