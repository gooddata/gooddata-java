package com.gooddata.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class ProjectValidationResultTest {

    @Test
    public void testDeserialize() throws Exception {
        final ProjectValidationResult log = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/project/project-validationResultLog.json"), ProjectValidationResult.class);

        assertThat(log.getCategory(), is("TRANSITIVITY"));
        assertThat(log.getLevel(), is("WARN"));
        assertThat(log.getMessage(), is("There are inconsistent values between %s->%s->%s and %s->%s for %s value '%s'."));
        assertThat(log.getParams(), hasSize(7));

    }
}