package com.gooddata.dataset.uploads;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddata.dataset.UploadStatus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.io.InputStream;

public class DatasetUploadsTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/dataset/uploads/data-set.json");
        final DatasetUploads datasetUploads = new ObjectMapper().readValue(input, DatasetUploads.class);

        assertThat(datasetUploads, notNullValue());

        assertThat(datasetUploads.getDatasetId(), is("dataset.campaign"));

        assertThat(datasetUploads.getUploadsUri(), is("/gdc/md/PROJECT_ID/data/uploads/814"));
        assertThat(datasetUploads.getLastSuccess(), is(new DateTime(2011, 9, 12, 13, 36, 27, DateTimeZone.UTC)));

        final DatasetUploads.LastUpload lastUpload = datasetUploads.getLastUpload();
        assertThat(lastUpload, notNullValue());
        assertThat(lastUpload.getUri(), is("/gdc/md/PROJECT_ID/data/upload/1076"));
        assertThat(lastUpload.getStatus(), is(UploadStatus.WARNING));
        assertThat(lastUpload.getProgress(), closeTo(1, 0.0001));
        assertThat(lastUpload.getMessage(), is("1 of 7 parts went with warnings"));
        assertThat(lastUpload.getDate(), is(new DateTime(2011, 10, 4, 10, 35, 5, DateTimeZone.UTC)));
    }
}
