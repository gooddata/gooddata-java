package com.gooddata.dataset.uploads;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.InputStream;

public class UploadsTests {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/dataset/uploads/uploads.json");
        final Uploads uploads = new ObjectMapper().readValue(input, Uploads.class);

        assertThat(uploads, notNullValue());
        assertThat(uploads.getUploads(), not(Matchers.<Upload>empty()));
        assertThat(uploads.getUploads().size(), is(2));
    }
}
