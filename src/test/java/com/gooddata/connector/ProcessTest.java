package com.gooddata.connector;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import static com.gooddata.connector.Status.Code.ERROR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ProcessTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final Process process = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/connector/process-error.json"), Process.class);

        assertThat(process, is(notNullValue()));
        assertThat(process.getFinished(), is(notNullValue()));
        assertThat(process.getStarted(), is(notNullValue()));
        assertThat(process.getStatus(), is(notNullValue()));
        assertThat(process.getStatus().getCode(), is(ERROR.name()));
        assertThat(process.getStatus().getDetail(), is("GDC-INTERNAL-ERROR"));
        assertThat(process.getStatus().getDescription(), is("Data load unsuccessful. Please check your settings and try again or contact us at support@gooddata.com"));
    }

}
