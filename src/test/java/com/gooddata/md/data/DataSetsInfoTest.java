package com.gooddata.md.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.io.InputStream;

public class DatasetsInfoTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/md/data/data-sets.json");
        final DatasetsInfo datasetsInfo = new ObjectMapper().readValue(input, DatasetsInfo.class);

        assertThat(datasetsInfo, notNullValue());
        assertThat(datasetsInfo.getDatasets(), notNullValue());
        assertThat(datasetsInfo.getDatasets().size(), is(2));

        final DatasetsInfo.Dataset dataset = datasetsInfo.getDatasets().get(0);
        assertThat(dataset, notNullValue());

        assertThat(dataset.getIdentifier(), is("dataset.campaign"));
        assertThat(dataset.getUri(), is("/gdc/md/PROJECT_ID/obj/814"));

        assertThat(dataset.getUploadConfigurationUri(), is("/configuration"));
        assertThat(dataset.getEtlInterfaceUri(), is("/etl/interface/uri/123"));
        assertThat(dataset.getDataUploadsUri(), is("/gdc/md/PROJECT_ID/data/uploads/814"));
        assertThat(dataset.getLastSuccess(), is(new DateTime(2011, 9, 12, 13, 36, 27, DateTimeZone.UTC)));
        assertThat(dataset.getLastUploadUri(), is("/gdc/md/PROJECT_ID/data/upload/1076"));
        assertThat(dataset.getLastUploadStatus(), is(DataUploadStatus.WARNING));
        assertThat((int) (dataset.getLastUploadProgress() * 100), is(100));
        assertThat(dataset.getLastUploadMessage(), is("1 of 7 parts went with warnings"));
        assertThat(dataset.getLastUploadDate(), is(new DateTime(2011, 10, 4, 10, 35, 5, DateTimeZone.UTC)));

        final DatasetsInfo.Dataset otherDataset = datasetsInfo.getDatasets().get(1);
        assertThat(otherDataset, notNullValue());
        assertThat(otherDataset.getLastUploadUri(), nullValue());
        assertThat(otherDataset.getLastUploadStatus(), nullValue());
        assertThat((int) (otherDataset.getLastUploadProgress() * 100), is(0));
        assertThat(otherDataset.getLastUploadMessage(), nullValue());
        assertThat(otherDataset.getLastUploadDate(), nullValue());
    }
}