package com.gooddata.dataset;

import com.gooddata.gdc.ErrorStructure;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class FailStatusTest {
    @Test
    public void deserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/dataset/upload_status_fail.json");
        final FailStatus value = new ObjectMapper().readValue(stream, FailStatus.class);
        assertThat(value, is(notNullValue()));
        assertThat(value.getStatus(), is("ERROR"));
        final ErrorStructure error = value.getError();
        assertThat(error, is(notNullValue()));
        assertThat(error.getMessage(), is("Manifest consist of columns that aren't in a single CSV file: f_person.nm_name"));
    }
}
