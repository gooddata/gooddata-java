package com.gooddata.dataset;

import com.gooddata.gdc.ErrorStructure;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class FailStatusTest {

    @Test
    public void testDeserialize() throws Exception {

        final InputStream stream = getClass().getResourceAsStream("/dataset/failStatus.json");
        final FailStatus value = new ObjectMapper().readValue(stream, FailStatus.class);

        assertThat(value, is(notNullValue()));
        assertThat(value.getStatus(), is("ERROR"));
        assertThat(value.getDate(), is("2014-04-21 00:11:34"));

        final ErrorStructure error = value.getError();
        assertThat(error, is(notNullValue()));
    }
}
