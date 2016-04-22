package com.gooddata.dataset.uploads;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.InputStream;

public class DatasetsUploadsTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/dataset/uploads/data-sets.json");
        final DatasetsUploads datasetsUploads = new ObjectMapper().readValue(input, DatasetsUploads.class);

        assertThat(datasetsUploads, notNullValue());
        assertThat(datasetsUploads.getDatasets(), not(Matchers.<DatasetUploads>empty()));
        assertThat(datasetsUploads.getDatasets().size(), is(2));
    }
}