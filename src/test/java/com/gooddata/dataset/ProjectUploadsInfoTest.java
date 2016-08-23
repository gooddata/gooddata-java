package com.gooddata.dataset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Collections;

public class ProjectUploadsInfoTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/dataset/uploads/data-sets.json");
        final ProjectUploadsInfo projectUploadsInfo = new ObjectMapper().readValue(input, ProjectUploadsInfo.class);

        assertThat(projectUploadsInfo, notNullValue());
        assertTrue(projectUploadsInfo.hasDataset("dataset.campaign"),
                "ProjectUploadsInfo must contain dataset with id: 'dataset.campaign'.");

        final DatasetUploadsInfo dataset = projectUploadsInfo.getDatasetUploadsInfo("dataset.campaign");

        assertThat(dataset, notNullValue());

        assertThat(dataset.getDatasetId(), is("dataset.campaign"));
        assertThat(dataset.getDatasetUri(), is("/gdc/md/PROJECT_ID/obj/814"));
        assertThat(dataset.getDatasetTitle(), is("Campaign"));
        assertThat(dataset.getDatasetAuthor(), is("/gdc/account/profile/1"));
        assertThat(dataset.getDatasetCreated(), is(new DateTime(2010, 11, 23, 10, 37, 54, DateTimeZone.UTC)));
        assertThat(dataset.getDatasetSummary(),
                is("Information about Medium, Campaign, Keyword, Ad Slot, Source and Referral Path"));

        assertThat(dataset.getUploadsUri(), is("/gdc/md/PROJECT_ID/data/uploads/814"));
        assertThat(dataset.getLastSuccess(), is(new DateTime(2011, 9, 12, 13, 36, 27, DateTimeZone.UTC)));

        final Upload lastUpload = dataset.getLastUpload();
        assertThat(lastUpload, notNullValue());
        assertThat(lastUpload.getUri(), is("/gdc/md/PROJECT_ID/data/upload/1076"));
        assertThat(lastUpload.getStatus(), is("WARNING"));
        assertThat(lastUpload.getProgress(), closeTo(1, 0.0001));
        assertThat(lastUpload.getMessage(), is("1 of 7 parts went with warnings"));
        assertThat(lastUpload.getCreatedAt(), is(new DateTime(2011, 10, 4, 10, 35, 5, DateTimeZone.UTC)));
    }

    @Test(expectedExceptions = DatasetNotFoundException.class)
    public void getDatasetUploadInfoFails() throws Exception {
        new ProjectUploadsInfo(Collections.<DatasetUploadsInfo>emptyList()).getDatasetUploadsInfo("dataset.non_existing_one");
    }
}