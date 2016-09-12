/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
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

public class UploadsInfoTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/dataset/uploads/data-sets.json");
        final UploadsInfo uploadsInfo = new ObjectMapper().readValue(input, UploadsInfo.class);

        assertThat(uploadsInfo, notNullValue());

        final UploadsInfo.DataSet dataset = uploadsInfo.getDataSet("dataset.campaign");

        assertThat(dataset, notNullValue());
        assertThat(dataset.getDatasetId(), is("dataset.campaign"));
        assertThat(dataset.getUploadsUri(), is("/gdc/md/PROJECT_ID/data/uploads/814"));
        assertThat(dataset.getLastUploadUri(), is("/gdc/md/PROJECT_ID/data/upload/1076"));
    }

    @Test(expectedExceptions = DatasetNotFoundException.class)
    public void getDatasetUploadInfoFails() throws Exception {
        new UploadsInfo(Collections.<UploadsInfo.DataSet>emptyList()).getDataSet("dataset.non_existing_one");
    }
}