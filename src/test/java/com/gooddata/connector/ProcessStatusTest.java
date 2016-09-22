/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static com.gooddata.connector.Status.Code.ERROR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ProcessStatusTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final ProcessStatus process = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/connector/process-status-error.json"), ProcessStatus.class);

        assertThat(process, is(notNullValue()));
        assertThat(process.getFinished(), is(notNullValue()));
        assertThat(process.getStarted(), is(notNullValue()));
        assertThat(process.getStatus(), is(notNullValue()));
        assertThat(process.getStatus().getCode(), is(ERROR.name()));
        assertThat(process.getStatus().getDetail(), is("GDC-INTERNAL-ERROR"));
        assertThat(process.getStatus().getDescription(), is("Data load unsuccessful. Please check your settings and try again or contact us at support@gooddata.com"));
        assertThat(process.getUri(), is("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS_ID"));
        assertThat(process.getId(), is("PROCESS_ID"));
    }

}
