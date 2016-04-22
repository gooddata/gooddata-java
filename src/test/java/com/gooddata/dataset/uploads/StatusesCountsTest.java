package com.gooddata.dataset.uploads;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddata.dataset.UploadStatus;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Map;

public class StatusesCountsTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/dataset/uploads/data-uploads-info.json");
        final StatusesCounts statusesCounts = new ObjectMapper().readValue(input, StatusesCounts.class);

        assertThat(statusesCounts, notNullValue());
        final Map<UploadStatus, Integer> map = statusesCounts.getStatusesMap();

        assertThat(map, notNullValue());
        assertThat(map.get(UploadStatus.OK), is(845));
        assertThat(map.get(UploadStatus.ERROR), is(25));
        assertThat(map.get(UploadStatus.RUNNING), is(1));
    }
}