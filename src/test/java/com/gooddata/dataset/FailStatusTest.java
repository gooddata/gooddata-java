package com.gooddata.dataset;

import com.gooddata.gdc.ErrorStructure;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class FailStatusTest {

    @Test
    public void testDeserialize() throws Exception {

        final InputStream stream = getClass().getResourceAsStream("/dataset/failStatus.json");
        final FailStatus value = new ObjectMapper().readValue(stream, FailStatus.class);

        assertThat(value, is(notNullValue()));
        assertThat(value.getStatus(), is("ERROR"));
        assertThat(value.getDate(), is(new DateTime(2014, 4, 21, 0, 11, 34, DateTimeZone.UTC)));

        final ErrorStructure error = value.getError();
        assertThat(error, is(notNullValue()));
    }

    @Test
    public void testDeserializeComplex() throws Exception {

        final InputStream stream = getClass().getResourceAsStream("/dataset/failStatusComplex.json");
        final FailStatus value = new ObjectMapper().readValue(stream, FailStatus.class);

        assertThat(value, is(notNullValue()));
        assertThat(value.getStatus(), is("ERROR"));
        assertThat(value.getDate(), is(new DateTime(2014, 11, 11, 22, 25, 11, DateTimeZone.UTC)));

        final ErrorStructure error = value.getError();
        assertThat(error, is(notNullValue()));

        assertThat(value.getErrorParts(), hasSize(2));
    }
}
